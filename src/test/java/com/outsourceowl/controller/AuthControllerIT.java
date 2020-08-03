package com.outsourceowl.controller;

import com.outsourceowl.dto.LoginDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;

public class AuthControllerIT extends ControllerBaseIT {
  private static final String SIGN_IN_URI = "/signin";

  @Before
  public void beforeTest() {
    super.setup();
  }

  @After
  public void afterTest() {
    super.clearDatabase();
  }

  @Test
  public void testAuthenticateUserWithValidData() {
    LoginDTO loginDTO = new LoginDTO(SELLER_EMAIL, USER_PASSWORD);

    ResponseEntity<String> response =
        restTemplate.exchange(
            createUrlWithPort(SIGN_IN_URI),
            HttpMethod.POST,
            new HttpEntity<>(loginDTO),
            String.class);

    assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
  }

  @Test
  public void testAuthenticateUserWithWrongPassword() {
    LoginDTO loginDTO = new LoginDTO(SELLER_USERNAME, "wrongPassword");

    ResponseEntity<String> response =
        restTemplate.exchange(
            createUrlWithPort(SIGN_IN_URI),
            HttpMethod.POST,
            new HttpEntity<>(loginDTO),
            String.class);

    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCodeValue());
  }

  @Test
  public void testAuthenticateUserWithNonexistentUsername() {
    LoginDTO loginDTO = new LoginDTO("wrongUsername", USER_PASSWORD);

    ResponseEntity<String> response =
        restTemplate.exchange(
            createUrlWithPort(SIGN_IN_URI),
            HttpMethod.POST,
            new HttpEntity<>(loginDTO),
            String.class);

    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCodeValue());
  }

  @Test
  public void testAuthenticateUserWithNonexistentEmail() {
    LoginDTO loginDTO = new LoginDTO("wrong@email.com", USER_PASSWORD);

    ResponseEntity<String> response =
        restTemplate.exchange(
            createUrlWithPort(SIGN_IN_URI),
            HttpMethod.POST,
            new HttpEntity<>(loginDTO),
            String.class);

    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCodeValue());
  }
}
