package com.outsourceowl.service;

import com.outsourceowl.dto.CurrentUserInfoDTO;
import com.outsourceowl.dto.LoginDTO;
import com.outsourceowl.dto.SignUpDTO;
import com.outsourceowl.exception.ForbiddenEventException;
import com.outsourceowl.exception.ResourceAlreadyExistsException;
import com.outsourceowl.exception.ResourceNotFoundException;
import com.outsourceowl.model.UserAccount;
import com.outsourceowl.model.UserRole;
import com.outsourceowl.model.constants.RoleType;
import com.outsourceowl.repository.RoleRepository;
import com.outsourceowl.repository.UserRepository;
import com.outsourceowl.security.CustomUserDetailsService;
import com.outsourceowl.security.JwtTokenProvider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class UserAccountService {
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider tokenProvider;
  private final CustomUserDetailsService customUserDetailsService;
  private final ModelMapper modelMapper;

  @Autowired
  public UserAccountService(
      AuthenticationManager authenticationManager,
      UserRepository userRepository,
      RoleRepository roleRepository,
      PasswordEncoder passwordEncoder,
      JwtTokenProvider tokenProvider,
      CustomUserDetailsService customUserDetailsService,
      ModelMapper modelMapper) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.tokenProvider = tokenProvider;
    this.customUserDetailsService = customUserDetailsService;
    this.modelMapper = modelMapper;
  }

  public String authenticate(LoginDTO loginDTO) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginDTO.getUsernameOrEmail(), loginDTO.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    return tokenProvider.generateToken(authentication);
  }

  public UserAccount registerUser(SignUpDTO signUpDTO) {
    if (userRepository.existsByUsername(signUpDTO.getUsername())) {
      throw new ResourceAlreadyExistsException("Username already in use");
    }

    if (userRepository.existsByEmail(signUpDTO.getEmail())) {
      throw new ResourceAlreadyExistsException("Email Address already in use");
    }

    UserAccount userAccount = modelMapper.map(signUpDTO, UserAccount.class);
    UserRole userRole = findOrCreateUserRole(signUpDTO.getRoleType());
    userAccount.setRole(userRole);
    userAccount.setPassword(passwordEncoder.encode(userAccount.getPassword()));

    return userRepository.save(userAccount);
  }

  public CurrentUserInfoDTO getCurrentUserInfo() {
    Long userId = customUserDetailsService.getAuthenticatedUser().getId();

    UserAccount userAccount =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    return modelMapper.map(userAccount, CurrentUserInfoDTO.class);
  }

  public long getCurrentUserId() {
    return customUserDetailsService.getAuthenticatedUser().getId();
  }

  public void validateLoggedUser(Long userId) {
    if (getCurrentUserId() != userId) {
      throw new ForbiddenEventException("Access to this resource is forbidden");
    }
  }

  public void addBalance(Long userId, Double amount) {
    validateLoggedUser(userId);
    UserAccount userAccount =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    userAccount.setBalance(userAccount.getBalance() + amount);
    userRepository.save(userAccount);
  }

  private UserRole findOrCreateUserRole(RoleType roleType) {
    Supplier<UserRole> saveRole =
        () -> roleRepository.save(UserRole.builder().roleType(roleType).build());
    return roleRepository.findByRoleType(roleType).orElseGet(saveRole);
  }
}
