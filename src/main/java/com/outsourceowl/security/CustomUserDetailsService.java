package com.outsourceowl.security;

import com.outsourceowl.exception.UnauthorizedEventException;
import com.outsourceowl.model.UserAccount;
import com.outsourceowl.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  private UserRepository userRepository;

  @Autowired
  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
    UserAccount userAccount =
        userRepository
            .findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        "UserAccount not found with username or email : " + usernameOrEmail));

    return UserPrincipal.create(userAccount);
  }

  @Transactional
  public UserDetails loadUserById(Long id) {
    UserAccount userAccount =
        userRepository
            .findById(id)
            .orElseThrow(
                () -> new UsernameNotFoundException("UserAccount not found with id : " + id));

    return UserPrincipal.create(userAccount);
  }

  public void validateAuthenticatedUserId(Long userId) {
    UserPrincipal authenticatedUser = getAuthenticatedUser();
    if (!authenticatedUser.getId().equals(userId)) {
      throw new UnauthorizedEventException();
    }
  }

  public UserPrincipal getAuthenticatedUser() {
    UserPrincipal user =
        (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (user == null) {
      throw new UnauthorizedEventException();
    }

    return user;
  }
}
