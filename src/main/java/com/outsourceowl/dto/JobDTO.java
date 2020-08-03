package com.outsourceowl.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobDTO {
  private Long id;

  private String name;

  private Double price;

  private Integer daysUntilDelivery;

  private Double rating;

  private String description;

  private String previewImage;

  private CategoryDTO category;

  private UserAccountDTO seller;
}
