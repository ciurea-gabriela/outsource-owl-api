package com.outsourceowl.dto;

import com.outsourceowl.model.constants.Status;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseUpdateDTO {

  private Integer rating;

  private String delivery;

  @NotNull
  private Status status;
}
