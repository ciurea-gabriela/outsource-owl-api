package com.outsourceowl.repository;

import com.outsourceowl.model.Job;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends PagingAndSortingRepository<Job, Long> {

  Optional<Job> findByIdAndSellerId(Long jobId, Long sellerId);

  List<Job> findAllByCategoryId(Long categoryId, Pageable pageable);

  List<Job> findAllBySellerId(Long sellerId);

  @Query(
      value = "select distinct on (category_id) id from Job Order By category_id",
      nativeQuery = true)
  List<Long> findAllDistinctCategoryName(Pageable pageable);

  List<Job> findAllByIdIn(List<Long> jobIds);
}
