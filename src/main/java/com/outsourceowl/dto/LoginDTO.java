package com.outsourceowl.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
  @NotBlank private String usernameOrEmail;

  @NotBlank
  @Size(min = 3, max = 20)
  private String password;
}
