package com.outsourceowl.controller;

import com.outsourceowl.dto.PurchaseCreateDTO;
import com.outsourceowl.dto.PurchaseDTO;
import com.outsourceowl.dto.PurchaseUpdateDTO;
import com.outsourceowl.model.Purchase;
import com.outsourceowl.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
public class PurchaseController {
  private final PurchaseService purchaseService;

  @Autowired
  public PurchaseController(PurchaseService purchaseService) {
    this.purchaseService = purchaseService;
  }

  @PostMapping("/purchases")
  @PreAuthorize("hasRole('ROLE_BUYER')")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity createPurchase(@Valid @RequestBody PurchaseCreateDTO purchaseCreateDTO) {
    Purchase purchase = purchaseService.createPurchase(purchaseCreateDTO);
    URI location =
        ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/purchases/{id}")
            .buildAndExpand(purchase.getId())
            .toUri();

    return ResponseEntity.created(location).build();
  }

  @GetMapping("/users/{userId}/purchases")
  public List<PurchaseDTO> getAllPurchasesByUser(@PathVariable Long userId) {
    return purchaseService.getAllPurchasesByUserId(userId);
  }

  @GetMapping("/users/{userId}/purchases/{purchaseId}")
  public PurchaseDTO getPurchaseByUser(@PathVariable Long userId, @PathVariable Long purchaseId) {
    return purchaseService.getPurchaseByUserIdAndPurchaseId(userId, purchaseId);
  }

  @PatchMapping("/users/{userId}/purchases/{purchaseId}")
  public void updatePurchaseStatus(
      @PathVariable Long userId,
      @PathVariable Long purchaseId,
      @Valid @RequestBody PurchaseUpdateDTO purchaseUpdateDTO) {
    purchaseService.updatePurchaseStatus(userId, purchaseId, purchaseUpdateDTO);
  }
}
