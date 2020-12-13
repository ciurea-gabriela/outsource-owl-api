package com.outsourceowl.controller;

import com.outsourceowl.dto.JobCreateDTO;
import com.outsourceowl.dto.JobDTO;
import com.outsourceowl.dto.JobUpdateDTO;
import com.outsourceowl.model.Category;
import com.outsourceowl.model.Job;
import com.outsourceowl.repository.CategoryRepository;
import com.outsourceowl.repository.JobRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JobControllerIT extends ControllerBaseIT {
  private Category category;

  @Autowired JobRepository jobRepository;
  @Autowired CategoryRepository categoryRepository;
  private static final String CATEGORY_NAME = "Category Test";
  private static final String JOB_NAME = "Job Name";
  private static final Double JOB_PRICE = 10.54D;
  private static final Integer JOB_DAYS_UNTIL_DELIVERY = 10;
  private static final String JOB_DESCRIPTION = "Test description";
  private static final Long INVALID_USER_ID = 99999L;
  private static final Long INVALID_CATEGORY_ID = 99999L;
  private static final Long INVALID_JOB_ID = 99999L;
  private static final String TEST_IMAGE_PATH = "static/files/user-images/test_job_file_image.PNG";

  @Before
  public void before() {
    this.category = createCategory(CATEGORY_NAME);
    super.setup();
  }

  @After
  public void after() {
    super.clearDatabase();
    categoryRepository.deleteAll();
  }

  @Test
  public void testCreateValidJob() throws IOException {
    HttpEntity<MultiValueMap<String, Object>> request = createJobRequestWithImage();
    ResponseEntity<String> response =
        restTemplate.postForEntity(createJobsUri(getSeller().getId()), request, String.class);

    List<Job> jobs = jobRepository.findAllBySellerId(getSeller().getId());

    assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
    assertEquals(JOB_NAME, jobs.get(0).getName());

    deleteImage(jobs);
  }

  @Test
  public void testCreateJobUnauthorized() {
    HttpEntity<MultiValueMap<String, Object>> request = createJobRequestWithImage();
    ResponseEntity<String> response =
        restTemplate.postForEntity(createJobsUri(INVALID_USER_ID), request, String.class);

    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCodeValue());
  }

  @Test
  public void testCreateJobWithoutAuthorizationHeaders() {
    JobCreateDTO jobCreateDTO =
        JobCreateDTO.builder()
            .name(JOB_NAME)
            .price(JOB_PRICE)
            .daysUntilDelivery(JOB_DAYS_UNTIL_DELIVERY)
            .categoryId(category.getId())
            .build();

    HttpEntity<JobCreateDTO> request = new HttpEntity<>(jobCreateDTO);
    ResponseEntity<String> response =
        restTemplate.postForEntity(createJobsUri(getSeller().getId()), request, String.class);

    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCodeValue());
  }

  @Test
  public void testCreateJobWithCategoryNotFound() {
    JobCreateDTO jobCreateDTO =
        JobCreateDTO.builder()
            .name(JOB_NAME)
            .price(JOB_PRICE)
            .daysUntilDelivery(JOB_DAYS_UNTIL_DELIVERY)
            .description(JOB_DESCRIPTION)
            .categoryId(INVALID_CATEGORY_ID)
            .build();

    HttpEntity<MultiValueMap<String, Object>> request = createJobRequestWithImage(jobCreateDTO);
    ResponseEntity<String> response =
        restTemplate.postForEntity(createJobsUri(getSeller().getId()), request, String.class);

    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
  }

  @Test
  public void testCreateJobWithForbiddenRoleType() {
    HttpEntity<MultiValueMap<String, Object>> request = createJobRequestWithImage();
    ResponseEntity<String> response =
        restTemplate.postForEntity(createJobsUri(getBuyer().getId()), request, String.class);

    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCodeValue());
  }

  @Test
  public void testUpdateJobSuccessfully() {
    final String UPDATED_JOB_NAME = "Updated Job Name";
    final Double UPDATED_JOB_PRICE = 90.00D;
    final Integer UPDATED_JOB_DAYS_UNTIL_DELIVER = 20;

    Job job = createJob();
    JobUpdateDTO updatedJob =
        JobUpdateDTO.builder()
            .name(UPDATED_JOB_NAME)
            .price(UPDATED_JOB_PRICE)
            .daysUntilDelivery(UPDATED_JOB_DAYS_UNTIL_DELIVER)
            .description("test123")
            .categoryId(category.getId())
            .build();

    HttpEntity<JobUpdateDTO> request =
        new HttpEntity<>(updatedJob, getSellerAuthorizationHeaders());
    ResponseEntity<JobCreateDTO> response =
        restTemplate.exchange(
            createJobsUri(getSeller().getId(), job.getId()),
            HttpMethod.PUT,
            request,
            JobCreateDTO.class);

    assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    assertEquals(UPDATED_JOB_NAME, response.getBody().getName());
    assertEquals(UPDATED_JOB_PRICE, response.getBody().getPrice());
    assertEquals(UPDATED_JOB_DAYS_UNTIL_DELIVER, response.getBody().getDaysUntilDelivery());
  }

  @Test
  public void testUpdateJobNotFound() {
    final String UPDATED_JOB_NAME = "Updated Job Name";
    final Double UPDATED_JOB_PRICE = 90.00D;
    final Integer UPDATED_JOB_DAYS_UNTIL_DELIVERY = 20;

    createJob();
    JobUpdateDTO updatedJob =
        JobUpdateDTO.builder()
            .name(UPDATED_JOB_NAME)
            .price(UPDATED_JOB_PRICE)
            .daysUntilDelivery(UPDATED_JOB_DAYS_UNTIL_DELIVERY)
            .description("")
            .categoryId(category.getId())
            .build();

    HttpEntity<JobUpdateDTO> request =
        new HttpEntity<>(updatedJob, getSellerAuthorizationHeaders());
    ResponseEntity<JobCreateDTO> response =
        restTemplate.exchange(
            createJobsUri(getSeller().getId(), INVALID_JOB_ID),
            HttpMethod.PUT,
            request,
            JobCreateDTO.class);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
  }

  @Test
  public void testUpdateJobCategoryNotFound() {
    final String UPDATED_JOB_NAME = "Updated Job Name";
    final Double UPDATED_JOB_PRICE = 90.00D;
    final Integer UPDATED_JOB_DAYS_UNTIL_DELIVERY = 20;

    Job job = createJob();
    JobUpdateDTO updatedJob =
        JobUpdateDTO.builder()
            .name(UPDATED_JOB_NAME)
            .price(UPDATED_JOB_PRICE)
            .daysUntilDelivery(UPDATED_JOB_DAYS_UNTIL_DELIVERY)
            .description(JOB_DESCRIPTION)
            .categoryId(INVALID_CATEGORY_ID)
            .build();

    HttpEntity<JobUpdateDTO> request =
        new HttpEntity<>(updatedJob, getSellerAuthorizationHeaders());
    ResponseEntity<JobCreateDTO> response =
        restTemplate.exchange(
            createJobsUri(getSeller().getId(), job.getId()),
            HttpMethod.PUT,
            request,
            JobCreateDTO.class);

    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
  }

  @Test
  public void testUpdateJobSellerNotFound() {
    final String UPDATED_JOB_NAME = "Updated Job Name";
    final Double UPDATED_JOB_PRICE = 90.00D;
    final Integer UPDATED_JOB_DAYS_UNTIL_DELIVER = 20;

    Job job = createJob();
    JobUpdateDTO updatedJob =
        JobUpdateDTO.builder()
            .name(UPDATED_JOB_NAME)
            .price(UPDATED_JOB_PRICE)
            .daysUntilDelivery(UPDATED_JOB_DAYS_UNTIL_DELIVER)
            .description("")
            .categoryId(category.getId())
            .build();

    HttpEntity<JobUpdateDTO> request =
        new HttpEntity<>(updatedJob, getSellerAuthorizationHeaders());
    ResponseEntity<JobCreateDTO> response =
        restTemplate.exchange(
            createJobsUri(INVALID_USER_ID, job.getId()),
            HttpMethod.PUT,
            request,
            JobCreateDTO.class);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
  }

  @Test
  public void testUpdateWithoutAuthorizationHeaders() {
    final String UPDATED_JOB_NAME = "Updated Job Name";
    final Double UPDATED_JOB_PRICE = 90.00D;
    final Integer UPDATED_JOB_DAYS_UNTIL_DELIVER = 20;

    Job job = createJob();
    JobUpdateDTO updatedJob =
        JobUpdateDTO.builder()
            .name(UPDATED_JOB_NAME)
            .price(UPDATED_JOB_PRICE)
            .daysUntilDelivery(UPDATED_JOB_DAYS_UNTIL_DELIVER)
            .description("")
            .categoryId(category.getId())
            .build();

    HttpEntity<JobUpdateDTO> request = new HttpEntity<>(updatedJob);
    ResponseEntity<JobCreateDTO> response =
        restTemplate.exchange(
            createJobsUri(getSeller().getId(), job.getId()),
            HttpMethod.PUT,
            request,
            JobCreateDTO.class);

    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCodeValue());
  }

  @Test
  public void testGetJobSuccessfully() {
    Job job = createJob();

    HttpEntity<HttpHeaders> request = new HttpEntity<>(getSellerAuthorizationHeaders());
    ResponseEntity<JobDTO> response =
        restTemplate.exchange(createJobUri(job.getId()), HttpMethod.GET, request, JobDTO.class);

    assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    assertEquals(JOB_NAME, response.getBody().getName());
    assertEquals(JOB_PRICE, response.getBody().getPrice());
    assertEquals(JOB_DAYS_UNTIL_DELIVERY, response.getBody().getDaysUntilDelivery());
  }

  @Test
  public void testGetJobNotFound() {
    createJob();

    HttpEntity<HttpHeaders> request = new HttpEntity<>(getSellerAuthorizationHeaders());
    ResponseEntity<JobDTO> response =
        restTemplate.exchange(createJobUri(INVALID_JOB_ID), HttpMethod.GET, request, JobDTO.class);

    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
  }

  @Test
  public void testGetJobWithoutAuthorizationHeaders() {
    Job job = createJob();

    HttpEntity<String> request = new HttpEntity<>("");
    ResponseEntity<JobDTO> response =
        restTemplate.exchange(
            createJobsUri(getSeller().getId(), job.getId()), HttpMethod.GET, request, JobDTO.class);

    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCodeValue());
  }

  @Test
  public void testDeleteJobSuccessfully() {
    Job job = createJob();

    HttpEntity<HttpHeaders> request = new HttpEntity<>(getSellerAuthorizationHeaders());
    ResponseEntity<Void> response =
        restTemplate.exchange(
            createJobsUri(getSeller().getId(), job.getId()),
            HttpMethod.DELETE,
            request,
            Void.class);

    assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCodeValue());
  }

  @Test
  public void testDeleteJobNotFound() {
    createJob();

    HttpEntity<String> request = new HttpEntity<>(getSellerAuthorizationHeaders());
    ResponseEntity<Void> response =
        restTemplate.exchange(
            createJobsUri(getSeller().getId(), INVALID_JOB_ID),
            HttpMethod.DELETE,
            request,
            Void.class);

    assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCodeValue());
  }

  @Test
  public void testDeleteJobSellerNotFound() {
    Job job = createJob();

    HttpEntity<HttpHeaders> request = new HttpEntity<>(getSellerAuthorizationHeaders());
    ResponseEntity<Void> response =
        restTemplate.exchange(
            createJobsUri(INVALID_JOB_ID, job.getId()), HttpMethod.DELETE, request, Void.class);

    assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatusCodeValue());
  }

  @Test
  public void testDeleteJobInvalidSellerId() {
    Job job = createJob();

    HttpEntity<HttpHeaders> request = new HttpEntity<>(getBuyerAuthorizationHeaders());
    ResponseEntity<Void> response =
        restTemplate.exchange(
            createJobsUri(INVALID_JOB_ID, job.getId()), HttpMethod.DELETE, request, Void.class);

    assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatusCodeValue());
  }

  @Test
  public void testDeleteJobForbidden() {
    Job job = createJob();

    HttpEntity<HttpHeaders> request = new HttpEntity<>(getBuyerAuthorizationHeaders());
    ResponseEntity<Void> response =
        restTemplate.exchange(
            createJobsUri(getBuyer().getId(), job.getId()), HttpMethod.DELETE, request, Void.class);

    assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatusCodeValue());
  }

  @Test
  public void testDeleteJobWithoutAuthorizationHeaders() {
    Job job = createJob();

    HttpEntity<String> request = new HttpEntity<>("");
    ResponseEntity<Void> response =
        restTemplate.exchange(
            createJobsUri(getSeller().getId(), job.getId()),
            HttpMethod.DELETE,
            request,
            Void.class);

    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCodeValue());
  }

  @Test
  public void testGetAllJobsBySellerIdNotEmpty() {
    final int NUMBER_OF_STORIES = 3;
    createJobs(NUMBER_OF_STORIES);

    HttpEntity<HttpHeaders> request = new HttpEntity<>(getSellerAuthorizationHeaders());
    ResponseEntity<List<JobDTO>> response =
        restTemplate.exchange(
            createJobsUri(getSeller().getId()),
            HttpMethod.GET,
            request,
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    assertEquals(NUMBER_OF_STORIES, response.getBody().size());
  }

  @Test
  public void testGetAllJobsBySellerIdListEmpty() {
    HttpEntity<HttpHeaders> request = new HttpEntity<>(getSellerAuthorizationHeaders());
    ResponseEntity<List<JobDTO>> response =
        restTemplate.exchange(
            createJobsUri(getSeller().getId()),
            HttpMethod.GET,
            request,
            new ParameterizedTypeReference<>() {});

    assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    assertEquals(0, response.getBody().size());
  }

  private Job createJob() {
    return jobRepository.save(
        Job.builder()
            .name(JOB_NAME)
            .price(JOB_PRICE)
            .rating(0.00D)
            .daysUntilDelivery(JOB_DAYS_UNTIL_DELIVERY)
            .description("test123")
            .category(category)
            .previewImage("image123")
            .seller(getSeller())
            .build());
  }

  private void createJobs(int numberOfJobs) {
    for (int i = 0; i < numberOfJobs; i++) {
      createJob();
    }
  }

  private String createJobsUri(Long userId) {
    return "/users/" + userId + "/jobs";
  }

  private String createJobsUri(Long userId, Long jobId) {
    return createJobsUri(userId) + "/" + jobId;
  }

  private String createJobUri(Long jobId) {
    return "/jobs/" + jobId;
  }

  private Category createCategory(String name) {
    if (categoryRepository.existsByName(name)) {
      return categoryRepository.findByName(name).orElse(null);
    } else {
      Category category = Category.builder().name(name).build();
      return categoryRepository.save(category);
    }
  }

  private void deleteImage(List<Job> jobs) throws IOException {
    String imageName = jobs.get(0).getPreviewImage();
    Path imageToDelete = Paths.get("src/main/resources/static/files/user-images/" + imageName);
    Files.delete(imageToDelete);
  }

  private HttpEntity<MultiValueMap<String, Object>> createJobRequestWithImage(
      JobCreateDTO jobCreateDTO) {
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("job", jobCreateDTO);
    body.add("file", new ClassPathResource((TEST_IMAGE_PATH)));

    HttpHeaders headers = getSellerAuthorizationHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    return new HttpEntity<>(body, headers);
  }

  private HttpEntity<MultiValueMap<String, Object>> createJobRequestWithImage() {
    JobCreateDTO jobCreateDTO =
        JobCreateDTO.builder()
            .name(JOB_NAME)
            .price(JOB_PRICE)
            .daysUntilDelivery(JOB_DAYS_UNTIL_DELIVERY)
            .description(JOB_DESCRIPTION)
            .categoryId(category.getId())
            .build();

    return createJobRequestWithImage(jobCreateDTO);
  }
}
