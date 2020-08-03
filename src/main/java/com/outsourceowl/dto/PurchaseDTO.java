package com.outsourceowl.dto;

import com.outsourceowl.model.constants.Status;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseDTO {
  private Long id;

  private Integer quantity;

  private Double price;

  private LocalDateTime creationDate;

  private LocalDateTime deliveryDate;

  private String description;

  private Integer rating;

  private String delivery;

  private Status status;

  private JobDTO job;

  private UserAccountDTO customer;

  private UserAccountDTO seller;
}
