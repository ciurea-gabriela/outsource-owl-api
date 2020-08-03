package com.outsourceowl.dto;

import com.outsourceowl.model.constants.RoleType;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserInfoDTO {
  private Long id;

  @NotBlank
  @Size(min = 5, max = 100)
  private String username;

  @NotBlank
  @Email
  @Size(max = 100)
  private String email;

  private Double rating;

  private Double balance;

  private RoleType userRole;
}
