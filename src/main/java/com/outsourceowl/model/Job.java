package com.outsourceowl.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "job")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank private String name;

  @NotNull private Double price;

  @NotNull private Integer daysUntilDelivery;

  @NotNull private Double rating = 0.00D;

  @NotBlank
  @Column(columnDefinition = "text")
  private String description;

  @NotBlank private String previewImage;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private UserAccount seller;

  @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<Purchase> purchases;
}
