package com.outsourceowl.repository;

import com.outsourceowl.model.PurchaseStatus;
import com.outsourceowl.model.constants.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchaseStatusRepository extends JpaRepository<PurchaseStatus, Long> {
  Optional<PurchaseStatus> findByStatus(Status status);
}
