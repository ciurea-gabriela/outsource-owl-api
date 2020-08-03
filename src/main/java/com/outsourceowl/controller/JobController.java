package com.outsourceowl.controller;

import com.outsourceowl.dto.JobCreateDTO;
import com.outsourceowl.dto.JobDTO;
import com.outsourceowl.dto.JobUpdateDTO;
import com.outsourceowl.model.Job;
import com.outsourceowl.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
public class JobController {
  private final JobService jobService;

  @Autowired
  public JobController(JobService jobService) {
    this.jobService = jobService;
  }

  @PostMapping(value = "/users/{id}/jobs", consumes = MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('ROLE_SELLER')")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity createJob(
      @RequestPart(value = "job") JobCreateDTO jobCreateDTO,
      @PathVariable("id") Long userId,
      @RequestPart(value = "file") MultipartFile file) {
    Job job = jobService.createJob(jobCreateDTO, userId, file);

    URI location =
        ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/users/{id}/jobs/{jobId}")
            .buildAndExpand(userId, job.getId())
            .toUri();

    return ResponseEntity.created(location).build();
  }

  @PutMapping("/users/{id}/jobs/{jobId}")
  @PreAuthorize("hasRole('ROLE_SELLER')")
  public JobCreateDTO updateJob(
      @Valid @RequestBody JobUpdateDTO updatedJob,
      @PathVariable("id") Long sellerId,
      @PathVariable("jobId") Long jobId) {
    return jobService.updateJob(updatedJob, sellerId, jobId);
  }

  @DeleteMapping("/users/{id}/jobs/{jobId}")
  @PreAuthorize("hasRole('ROLE_SELLER')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteJob(@PathVariable("id") Long sellerId, @PathVariable("jobId") Long jobId) {
    jobService.deleteJob(sellerId, jobId);
  }

  @GetMapping("/users/{id}/jobs")
  @PreAuthorize("hasRole('ROLE_SELLER')")
  public List<JobDTO> getAllJobsBySellerId(@PathVariable("id") Long sellerId) {
    return jobService.getAllJobsBySellerId(sellerId);
  }

  @GetMapping("/jobs")
  public List<JobDTO> getAllJobsWithPagination(
      @PageableDefault(size = 20)
          @SortDefault.SortDefaults({
            @SortDefault(sort = "rating", direction = Sort.Direction.DESC),
            @SortDefault(sort = "price", direction = Sort.Direction.ASC)
          })
          Pageable pageable,
      @RequestParam Boolean distinct,
      @RequestParam(required = false) Long categoryId) {
    return jobService.getAllJobsWithPagination(pageable, distinct, categoryId);
  }

  @GetMapping("/jobs/{id}")
  public JobDTO getJob(@PathVariable("id") Long jobId) {
    return jobService.getJob(jobId);
  }
}
