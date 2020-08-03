package com.outsourceowl.controller;

import com.outsourceowl.dto.CurrentUserInfoDTO;
import com.outsourceowl.dto.SignUpDTO;
import com.outsourceowl.model.constants.RoleType;
import com.sun.net.httpserver.Headers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.*;

import static org.junit.Assert.assertEquals;

public class UserAccountControllerIT extends ControllerBaseIT {
  private static final String USERNAME = "adamsmith";
  private static final String EMAIL = "adam@email.com";
  private static final String SIGN_UP_URI = "/users";
  private static final String CURRENT_USER_INFO_URI = "/users/current-info";

  @Before
  public void beforeTest() {
    super.setup();
  }

  @After
  public void afterTest() {
    super.clearDatabase();
  }

  @Test
  public void testRegisterUserWithValidData() {
    SignUpDTO signUpDTO = new SignUpDTO(USERNAME, EMAIL, USER_PASSWORD, RoleType.ROLE_SELLER);

    ResponseEntity<String> response =
        restTemplate.exchange(
            createUrlWithPort(SIGN_UP_URI),
            HttpMethod.POST,
            new HttpEntity<>(signUpDTO),
            String.class);

    assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
  }

  @Test
  public void testRegisterUserWithUsernameTaken() {
    SignUpDTO signUpDTO =
        new SignUpDTO(SELLER_USERNAME, "another@email.com", USER_PASSWORD, RoleType.ROLE_SELLER);

    ResponseEntity<String> response =
        restTemplate.exchange(
            createUrlWithPort(SIGN_UP_URI),
            HttpMethod.POST,
            new HttpEntity<>(signUpDTO),
            String.class);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
  }

  @Test
  public void testRegisterUserWithEmailTaken() {
    SignUpDTO signUpDTO =
        new SignUpDTO(USERNAME, SELLER_EMAIL, USER_PASSWORD, RoleType.ROLE_SELLER);

    ResponseEntity<String> response =
        restTemplate.exchange(
            createUrlWithPort(SIGN_UP_URI),
            HttpMethod.POST,
            new HttpEntity<>(signUpDTO),
            String.class);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
  }

  @Test
  public void testRegisterUserInvalidUsername() {
    SignUpDTO signUpDTO = new SignUpDTO("ad", SELLER_EMAIL, USER_PASSWORD, RoleType.ROLE_SELLER);

    ResponseEntity<String> response =
        restTemplate.exchange(
            createUrlWithPort(SIGN_UP_URI),
            HttpMethod.POST,
            new HttpEntity<>(signUpDTO),
            String.class);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
  }

  @Test
  public void testRegisterUserInvalidEmail() {
    SignUpDTO signUpDTO =
        new SignUpDTO(SELLER_USERNAME, "adamsmith.com", USER_PASSWORD, RoleType.ROLE_SELLER);

    ResponseEntity<String> response =
        restTemplate.exchange(
            createUrlWithPort(SIGN_UP_URI),
            HttpMethod.POST,
            new HttpEntity<>(signUpDTO),
            String.class);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
  }

  @Test
  public void testRegisterUserInvalidPassword() {
    SignUpDTO signUpDTO = new SignUpDTO(SELLER_USERNAME, SELLER_EMAIL, "", RoleType.ROLE_SELLER);

    ResponseEntity<String> response =
        restTemplate.exchange(
            createUrlWithPort(SIGN_UP_URI),
            HttpMethod.POST,
            new HttpEntity<>(signUpDTO),
            String.class);

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
  }

  @Test
  public void testGetCurrentUserInfoSuccessful() {
    var request = new HttpEntity<>(getSellerAuthorizationHeaders());

    ResponseEntity<CurrentUserInfoDTO> response =
        restTemplate.exchange(
            createUrlWithPort(CURRENT_USER_INFO_URI),
            HttpMethod.GET,
            request,
            CurrentUserInfoDTO.class);

    assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    assertEquals(SELLER_USERNAME, response.getBody().getUsername());
    assertEquals(SELLER_EMAIL, response.getBody().getEmail());
  }

  @Test
  public void testGetCurrentUserInfoUnauthorized() {
    Headers headers = new Headers();
    headers.set(HttpHeaders.AUTHORIZATION, "123");
    var request = new HttpEntity<>(headers);

    ResponseEntity<CurrentUserInfoDTO> response =
        restTemplate.exchange(
            createUrlWithPort(CURRENT_USER_INFO_URI),
            HttpMethod.GET,
            request,
            CurrentUserInfoDTO.class);

    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCodeValue());
  }
}
