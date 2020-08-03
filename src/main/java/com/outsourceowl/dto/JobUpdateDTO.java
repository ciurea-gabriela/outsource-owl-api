package com.outsourceowl.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobUpdateDTO {
  @NotBlank private String name;

  @NotNull private Double price;

  @NotNull private Integer daysUntilDelivery;

  @NotBlank private String description;

  @NotNull private Long categoryId;
}
