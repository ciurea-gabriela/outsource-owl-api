package com.outsourceowl.dto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseCreateDTO {

  @NotBlank private String description;

  @NotNull
  @Min(1)
  @Max(10)
  private Integer quantity;

  @NotNull
  private Long jobId;

  @NotNull
  private Long customerId;
}
