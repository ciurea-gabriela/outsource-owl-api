package com.outsourceowl.controller;

import com.outsourceowl.dto.CurrentUserInfoDTO;
import com.outsourceowl.dto.LoginDTO;
import com.outsourceowl.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AuthController {
  private final UserAccountService userAccountService;

  @Autowired
  public AuthController(UserAccountService userAccountService) {
    this.userAccountService = userAccountService;
  }

  @PostMapping("/signin")
  public ResponseEntity<CurrentUserInfoDTO> authenticateUser(
      @Valid @RequestBody LoginDTO loginDTO) {
    String jwt = userAccountService.authenticate(loginDTO);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
    httpHeaders.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.AUTHORIZATION);
    CurrentUserInfoDTO currentUserInfoDTO = userAccountService.getCurrentUserInfo();

    return new ResponseEntity<>(currentUserInfoDTO, httpHeaders, HttpStatus.OK);
  }
}
