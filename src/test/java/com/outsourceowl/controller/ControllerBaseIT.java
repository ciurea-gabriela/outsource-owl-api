package com.outsourceowl.controller;

import com.outsourceowl.dto.LoginDTO;
import com.outsourceowl.model.UserAccount;
import com.outsourceowl.model.UserRole;
import com.outsourceowl.model.constants.RoleType;
import com.outsourceowl.repository.RoleRepository;
import com.outsourceowl.repository.UserRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class ControllerBaseIT {
  @Autowired protected TestRestTemplate restTemplate;
  @LocalServerPort protected int port;

  private UserAccount seller;
  private UserAccount buyer;
  private HttpHeaders sellerAuthorizationHeaders;
  private HttpHeaders buyerAuthorizationHeaders;

  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private RoleRepository roleRepository;

  private static final String SIGN_IN_URI = "/signin";
  protected static final String SELLER_USERNAME = "seller";
  protected static final String SELLER_EMAIL = "seller@email.com";
  protected static final String BUYER_USERNAME = "buyer";
  protected static final String BUYER_EMAIL = "buyer@email.com";
  protected static final String USER_PASSWORD = "password";

  protected void setup() {
    this.seller = createUser(SELLER_USERNAME, SELLER_EMAIL, RoleType.ROLE_SELLER);
    this.buyer = createUser(BUYER_USERNAME, BUYER_EMAIL, RoleType.ROLE_BUYER);
    sellerAuthorizationHeaders = setupAuthorizationHeaders(seller);
    buyerAuthorizationHeaders = setupAuthorizationHeaders(buyer);
  }

  protected void clearDatabase() {
    userRepository.deleteAll();
  }

  protected UserAccount createUser(String username, String email, RoleType roleType) {
    if (userRepository.existsByUsername(username)) {
      return userRepository.findByUsernameOrEmail(username, email).orElse(null);
    } else {
      UserAccount userAccount =
          UserAccount.builder()
              .username(username)
              .email(email)
              .password(passwordEncoder.encode(USER_PASSWORD))
              .rating(0.00D)
              .balance(0.00D)
              .build();
      UserRole userRole =
          roleRepository
              .findByRoleType(roleType)
              .orElseGet(() -> roleRepository.save(UserRole.builder().roleType(roleType).build()));

      userAccount.setRole(userRole);
      return userRepository.save(userAccount);
    }
  }

  private HttpHeaders setupAuthorizationHeaders(UserAccount userAccount) {
    LoginDTO loginDTO = new LoginDTO(userAccount.getUsername(), USER_PASSWORD);

    ResponseEntity<String> response =
        restTemplate.exchange(
            createUrlWithPort(SIGN_IN_URI),
            HttpMethod.POST,
            new HttpEntity<>(loginDTO),
            String.class);

    String jwt =
        Optional.ofNullable(response.getHeaders().get(HttpHeaders.AUTHORIZATION))
            .map(a -> a.get(0))
            .orElse("");

    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.AUTHORIZATION, jwt);
    return headers;
  }

  protected UserAccount getSeller() {
    return this.seller;
  }

  protected UserAccount getBuyer() {
    return this.buyer;
  }

  protected HttpHeaders getSellerAuthorizationHeaders() {
    return this.sellerAuthorizationHeaders;
  }

  protected HttpHeaders getBuyerAuthorizationHeaders() {
    return this.buyerAuthorizationHeaders;
  }

  protected String createUrlWithPort(String uri) {
    return "http://localhost:" + port + uri;
  }
}
