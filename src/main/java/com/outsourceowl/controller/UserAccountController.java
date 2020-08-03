package com.outsourceowl.controller;

import com.outsourceowl.dto.BalanceDTO;
import com.outsourceowl.dto.CurrentUserInfoDTO;
import com.outsourceowl.dto.SignUpDTO;
import com.outsourceowl.model.UserAccount;
import com.outsourceowl.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
public class UserAccountController {
  private final UserAccountService userAccountService;

  @Autowired
  public UserAccountController(UserAccountService userAccountService) {
    this.userAccountService = userAccountService;
  }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity registerUser(@Valid @RequestBody SignUpDTO signUpDTO) {
    UserAccount userAccount = userAccountService.registerUser(signUpDTO);

    URI location =
        ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/users/{id}")
            .buildAndExpand(userAccount.getId())
            .toUri();

    return ResponseEntity.created(location).build();
  }

  @PostMapping("/users/{id}/balance")
  @PreAuthorize("hasRole('ROLE_BUYER')")
  public void patchUser(
      @PathVariable("id") Long userId, @Valid @RequestBody BalanceDTO balanceDTO) {
    userAccountService.addBalance(userId, balanceDTO.getBalance());
  }

  @GetMapping("/users/current-info")
  public CurrentUserInfoDTO getCurrentUserInfo() {
    return userAccountService.getCurrentUserInfo();
  }
}
