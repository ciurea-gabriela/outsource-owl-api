package com.outsourceowl.model;

import lombok.*;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Range(min = 1, max = 10)
  private Integer quantity;

  @NotNull private Double price;

  @NotNull private LocalDateTime creationDate;

  @NotNull private LocalDateTime deliveryDate;

  @NotBlank private String description;

  private Integer rating;

  @Column(columnDefinition = "text")
  private String delivery;

  @NotNull
  @OneToOne
  @JoinColumn(name = "status_id")
  private PurchaseStatus status;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Job job;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private UserAccount customer;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private UserAccount seller;
}
