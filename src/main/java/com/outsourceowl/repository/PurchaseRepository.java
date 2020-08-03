package com.outsourceowl.repository;

import com.outsourceowl.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

  List<Purchase> findAllBySellerId(Long sellerId);

  List<Purchase> findAllByCustomerId(Long buyerId);

  List<Purchase> findAllByJobId(Long jobId);
}
