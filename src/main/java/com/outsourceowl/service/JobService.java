package com.outsourceowl.service;

import com.outsourceowl.dto.JobCreateDTO;
import com.outsourceowl.dto.JobDTO;
import com.outsourceowl.dto.JobUpdateDTO;
import com.outsourceowl.dto.JobsSizeDTO;
import com.outsourceowl.exception.ResourceNotFoundException;
import com.outsourceowl.model.Category;
import com.outsourceowl.model.Job;
import com.outsourceowl.model.UserAccount;
import com.outsourceowl.repository.CategoryRepository;
import com.outsourceowl.repository.JobRepository;
import com.outsourceowl.repository.UserRepository;
import com.outsourceowl.security.CustomUserDetailsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JobService {
  private final JobRepository jobRepository;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;
  private final CustomUserDetailsService customUserDetailsService;
  private final ModelMapper modelMapper;
  private final FileStorageService fileStorageService;

  @Autowired
  public JobService(
      JobRepository jobRepository,
      UserRepository userRepository,
      CategoryRepository categoryRepository,
      CustomUserDetailsService customUserDetailsService,
      ModelMapper modelMapper,
      FileStorageService fileStorageService) {
    this.jobRepository = jobRepository;
    this.userRepository = userRepository;
    this.categoryRepository = categoryRepository;
    this.customUserDetailsService = customUserDetailsService;
    this.modelMapper = modelMapper;
    this.fileStorageService = fileStorageService;
  }

  public Job createJob(JobCreateDTO jobCreateDTO, Long sellerId, MultipartFile file) {
    customUserDetailsService.validateAuthenticatedUserId(sellerId);

    UserAccount userAccount =
        userRepository
            .findById(sellerId)
            .orElseThrow(
                () -> new ResourceNotFoundException("User not found with id: " + sellerId));

    UUID uuid = UUID.randomUUID();
    String generatedFileName = uuid.toString() + Objects.hashCode(userAccount.getUsername());
    String fileName = fileStorageService.storeFile(file, generatedFileName);

    Category category =
        categoryRepository
            .findById(jobCreateDTO.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

    Job job = modelMapper.map(jobCreateDTO, Job.class);
    job.setPreviewImage(fileName);
    job.setCategory(category);
    job.setSeller(userAccount);
    job = jobRepository.save(job);

    userAccount.getJobs().add(job);
    userRepository.save(userAccount);

    return job;
  }

  public JobCreateDTO updateJob(JobUpdateDTO updatedJob, Long sellerId, Long jobId) {
    Job job =
        jobRepository
            .findByIdAndSellerId(jobId, sellerId)
            .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

    Category category =
        categoryRepository
            .findById(updatedJob.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

    job.setName(updatedJob.getName());
    job.setPrice(updatedJob.getPrice());
    job.setDaysUntilDelivery(updatedJob.getDaysUntilDelivery());
    job.setDescription(updatedJob.getDescription());
    job.setCategory(category);

    return modelMapper.map(jobRepository.save(job), JobCreateDTO.class);
  }

  public JobDTO getJob(Long jobId) {
    Job job =
        jobRepository
            .findById(jobId)
            .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

    return modelMapper.map(job, JobDTO.class);
  }

  public JobsSizeDTO getJobsSize() {
    long size = jobRepository.count();
    return new JobsSizeDTO(size);
  }

  public void deleteJob(Long sellerId, Long jobId) {
    jobRepository.findByIdAndSellerId(jobId, sellerId).ifPresent(jobRepository::delete);
  }

  public List<JobDTO> getAllJobsBySellerId(Long sellerId) {
    return jobRepository.findAllBySellerId(sellerId).stream()
        .map(job -> modelMapper.map(job, JobDTO.class))
        .collect(Collectors.toList());
  }

  public List<JobDTO> getAllJobsWithPagination(
      Pageable pagination, Boolean distinct, Long categoryId) {
    if (categoryId != null && !distinct) {
      return jobRepository.findAllByCategoryId(categoryId, pagination).stream()
          .map(job -> modelMapper.map(job, JobDTO.class))
          .collect(Collectors.toList());
    }

    if (distinct != null && distinct) {
      List<Long> jobIds = jobRepository.findAllDistinctCategoryName(pagination);
      return jobRepository.findAllByIdIn(jobIds).stream()
          .map(job -> modelMapper.map(job, JobDTO.class))
          .collect(Collectors.toList());
    }

    return jobRepository.findAll(pagination).stream()
        .map(job -> modelMapper.map(job, JobDTO.class))
        .collect(Collectors.toList());
  }
}
