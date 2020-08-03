package com.outsourceowl.dto;

import com.outsourceowl.model.constants.RoleType;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpDTO {
  @NotBlank
  @Size(min = 5, max = 100)
  private String username;

  @NotBlank
  @Email
  @Size(max = 100)
  private String email;

  @NotBlank private String password;

  @NotNull private RoleType roleType;
}
