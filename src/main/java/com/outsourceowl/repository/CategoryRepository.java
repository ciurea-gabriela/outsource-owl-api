package com.outsourceowl.repository;

import com.outsourceowl.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  Boolean existsByName(String name);

  Optional<Category> findByName(String name);
}
