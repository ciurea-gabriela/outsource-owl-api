package com.outsourceowl.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccountDTO {
  private String username;

  private Double rating;
}
