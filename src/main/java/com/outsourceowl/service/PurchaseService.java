package com.outsourceowl.service;

import com.google.common.collect.ImmutableList;
import com.outsourceowl.dto.PurchaseCreateDTO;
import com.outsourceowl.dto.PurchaseDTO;
import com.outsourceowl.dto.PurchaseUpdateDTO;
import com.outsourceowl.exception.ForbiddenEventException;
import com.outsourceowl.exception.ResourceNotFoundException;
import com.outsourceowl.exception.ValidationException;
import com.outsourceowl.model.Job;
import com.outsourceowl.model.Purchase;
import com.outsourceowl.model.PurchaseStatus;
import com.outsourceowl.model.UserAccount;
import com.outsourceowl.model.constants.RoleType;
import com.outsourceowl.model.constants.Status;
import com.outsourceowl.repository.JobRepository;
import com.outsourceowl.repository.PurchaseRepository;
import com.outsourceowl.repository.PurchaseStatusRepository;
import com.outsourceowl.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import static com.outsourceowl.model.constants.Status.*;

@Service
public class PurchaseService {

  private final PurchaseRepository purchaseRepository;
  private final PurchaseStatusRepository purchaseStatusRepository;
  private final UserAccountService userAccountService;
  private final UserRepository userRepository;
  private final JobRepository jobRepository;
  private final ModelMapper modelMapper;

  @Autowired
  public PurchaseService(
      PurchaseRepository purchaseRepository,
      PurchaseStatusRepository purchaseStatusRepository,
      UserAccountService userAccountService,
      UserRepository userRepository,
      JobRepository jobRepository,
      ModelMapper modelMapper) {
    this.purchaseRepository = purchaseRepository;
    this.purchaseStatusRepository = purchaseStatusRepository;
    this.userAccountService = userAccountService;
    this.userRepository = userRepository;
    this.jobRepository = jobRepository;
    this.modelMapper = modelMapper;
  }

  @Transactional
  public Purchase createPurchase(PurchaseCreateDTO purchaseCreateDTO) {
    userAccountService.validateLoggedUser(purchaseCreateDTO.getCustomerId());
    UserAccount userAccount =
        userRepository
            .findById(purchaseCreateDTO.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    Job job =
        jobRepository
            .findById(purchaseCreateDTO.getJobId())
            .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

    double purchasePrice = purchaseCreateDTO.getQuantity() * job.getPrice();
    if (userAccount.getBalance() < purchasePrice) {
      throw new ValidationException("Account balance is too small");
    } else {
      userAccount.setBalance(userAccount.getBalance() - purchasePrice);
      userAccount = userRepository.save(userAccount);
    }

    PurchaseStatus purchaseStatus =
        purchaseStatusRepository
            .findByStatus(Status.IN_PROGRESS)
            .orElseThrow(() -> new ResourceNotFoundException("Status not found"));

    LocalDateTime creationDate = LocalDateTime.now();

    Purchase purchase =
        Purchase.builder()
            .quantity(purchaseCreateDTO.getQuantity())
            .price(purchasePrice)
            .creationDate(creationDate)
            .deliveryDate(creationDate.plusDays(job.getDaysUntilDelivery()))
            .description(purchaseCreateDTO.getDescription())
            .status(purchaseStatus)
            .job(job)
            .customer(userAccount)
            .seller(job.getSeller())
            .build();

    return purchaseRepository.save(purchase);
  }

  public List<PurchaseDTO> getAllPurchasesByUserId(Long userId) {
    userAccountService.validateLoggedUser(userId);

    UserAccount userAccount =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    List<Purchase> purchases;

    if (userAccount.getRole().getRoleType() == RoleType.ROLE_SELLER) {
      purchases = purchaseRepository.findAllBySellerId(userId);
    } else {
      purchases = purchaseRepository.findAllByCustomerId(userId);
    }

    updateLatePurchaseIfNecessary(purchases);
    return purchases.stream()
        .map(p -> modelMapper.map(p, PurchaseDTO.class))
        .collect(Collectors.toList());
  }

  public PurchaseDTO getPurchaseByUserIdAndPurchaseId(Long userId, Long purchaseId) {
    userAccountService.validateLoggedUser(userId);

    Purchase purchase =
        purchaseRepository
            .findById(purchaseId)
            .orElseThrow(() -> new ResourceNotFoundException("Purchase not found"));

    validatePurchaseRequester(userId, purchase);
    updateLatePurchaseIfNecessary(ImmutableList.of(purchase));

    return modelMapper.map(purchase, PurchaseDTO.class);
  }

  @Transactional
  public void updatePurchaseStatus(
      Long userId, Long purchaseId, PurchaseUpdateDTO purchaseUpdateDTO) {
    userAccountService.validateLoggedUser(userId);
    Purchase purchase =
        purchaseRepository.findById(purchaseId).orElseThrow(ForbiddenEventException::new);
    validatePurchaseRequester(userId, purchase);

    if (purchase.getStatus().getStatus() == DELIVERED
        && purchaseUpdateDTO.getStatus() == FINISHED) {
      finishPurchase(userId, purchase, purchaseUpdateDTO);
      return;
    }

    if (purchase.getStatus().getStatus() == FINISHED && purchaseUpdateDTO.getStatus() == RATED) {
      ratePurchase(userId, purchase, purchaseUpdateDTO);
      return;
    }

    if (purchase.getStatus().getStatus() == IN_PROGRESS
        || purchase.getStatus().getStatus() == LATE) {
      switch (purchaseUpdateDTO.getStatus()) {
        case DELIVERED:
          deliverPurchase(userId, purchase, purchaseUpdateDTO);
          break;
        case CANCELED:
          cancelPurchase(purchase);
          break;
        default:
          break;
      }
    }
  }

  private void deliverPurchase(
      Long userId, Purchase purchase, PurchaseUpdateDTO purchaseUpdateDTO) {
    if (!isRequestDoneBySeller(userId, purchase)) {
      throw new ForbiddenEventException();
    }
    validateDeliveryContent(purchaseUpdateDTO);
    setPurchaseStatus(purchase, DELIVERED);
    purchase.setDelivery(purchaseUpdateDTO.getDelivery());
    purchaseRepository.save(purchase);
  }

  private void finishPurchase(Long userId, Purchase purchase, PurchaseUpdateDTO purchaseUpdateDTO) {
    if (!isRequestDoneByCustomer(userId, purchase)) {
      throw new ForbiddenEventException();
    }
    setPurchaseStatus(purchase, FINISHED);
    Optional.ofNullable(purchaseUpdateDTO.getRating()).ifPresent(purchase::setRating);
    purchaseRepository.save(purchase);
    UserAccount seller = purchase.getJob().getSeller();
    seller.setBalance(seller.getBalance() + purchase.getPrice());
    userRepository.save(seller);
  }

  private void ratePurchase(Long userId, Purchase purchase, PurchaseUpdateDTO purchaseUpdateDTO) {
    if (!isRequestDoneByCustomer(userId, purchase)) {
      throw new ForbiddenEventException();
    }
    purchase.setRating(purchaseUpdateDTO.getRating());
    purchaseRepository.save(purchase);
    calculateNewRating(purchase);
  }

  private void calculateNewRating(Purchase purchase) {
    UserAccount seller = purchase.getSeller();
    OptionalDouble sellerJobRating =
        purchaseRepository.findAllByJobId(purchase.getJob().getId()).stream()
            .filter(p -> p.getRating() != null && p.getRating() != 0)
            .mapToInt(Purchase::getRating)
            .average();

    OptionalDouble sellerRating =
        jobRepository.findAllBySellerId(seller.getId()).stream()
            .filter(p -> p.getRating() != null && p.getRating() != 0)
            .mapToDouble(Job::getRating)
            .average();

    sellerJobRating.ifPresent(rating -> purchase.getJob().setRating(round(rating, 1)));
    sellerRating.ifPresent(rating -> seller.setRating(round(rating, 1)));
    jobRepository.save(purchase.getJob());
    purchaseRepository.save(purchase);
    userRepository.save(seller);
  }

  private void cancelPurchase(Purchase purchase) {
    UserAccount customer = purchase.getCustomer();
    customer.setBalance(customer.getBalance() + purchase.getPrice());
    setPurchaseStatus(purchase, CANCELED);
    purchaseRepository.save(purchase);
    userRepository.save(customer);
  }

  private void setPurchaseStatus(Purchase purchase, Status status) {
    PurchaseStatus purchaseStatus =
        purchaseStatusRepository
            .findByStatus(status)
            .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
    purchase.setStatus(purchaseStatus);
  }

  private void validateDeliveryContent(PurchaseUpdateDTO purchaseUpdateDTO) {
    if (purchaseUpdateDTO.getDelivery() == null || purchaseUpdateDTO.getDelivery().isBlank()) {
      throw new ValidationException("Delivery content can't be empty");
    }
  }

  private void updateLatePurchaseIfNecessary(List<Purchase> purchases) {
    purchases.forEach(
        p -> {
          if (IN_PROGRESS.equals(p.getStatus().getStatus())
              && p.getDeliveryDate().isBefore(LocalDateTime.now())) {
            PurchaseStatus purchaseStatus =
                purchaseStatusRepository
                    .findByStatus(Status.LATE)
                    .orElseThrow(() -> new ResourceNotFoundException("Status not found"));
            p.setStatus(purchaseStatus);
            purchaseRepository.save(p);
          }
        });
  }

  private void validatePurchaseRequester(Long userId, Purchase purchase) {
    boolean isPurchaseRequestedBySeller = userId.equals(purchase.getJob().getSeller().getId());
    boolean isPurchaseRequestedByCustomer = userId.equals(purchase.getCustomer().getId());

    if (!isPurchaseRequestedBySeller && !isPurchaseRequestedByCustomer) {
      throw new ForbiddenEventException();
    }
  }

  private boolean isRequestDoneByCustomer(Long userId, Purchase purchase) {
    return userId.equals(purchase.getCustomer().getId());
  }

  private boolean isRequestDoneBySeller(Long userId, Purchase purchase) {
    return userId.equals(purchase.getJob().getSeller().getId());
  }

  private double round(double value, int precision) {
    int scale = (int) Math.pow(10, precision);
    return (double) Math.round(value * scale) / scale;
  }
}
