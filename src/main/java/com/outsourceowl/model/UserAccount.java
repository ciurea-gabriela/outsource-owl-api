package com.outsourceowl.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "user_account")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(min = 5, max = 100)
  private String username;

  @NotBlank
  @Email
  @Size(max = 100)
  private String email;

  @NotBlank private String password;

  @NotNull private Double rating = 0.00D;

  @NotNull private Double balance = 0.00D;

  @NotNull
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "role_id")
  private UserRole role;

  @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<Job> jobs;

  @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<Purchase> purchases;
}
