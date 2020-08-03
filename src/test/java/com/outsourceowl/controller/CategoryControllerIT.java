package com.outsourceowl.controller;

import com.outsourceowl.dto.CategoryDTO;
import com.outsourceowl.model.Category;
import com.outsourceowl.repository.CategoryRepository;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CategoryControllerIT extends ControllerBaseIT {
  @Autowired CategoryRepository categoryRepository;
  private static final String CATEGORY_URI = "/categories";

  @After
  public void after() {
    categoryRepository.deleteAll();
  }

  @Test
  public void testGetAllCategories() {
    final int NUMBER_OF_CATEGORIES = 5;
    createCategories(NUMBER_OF_CATEGORIES);

    ResponseEntity<List<CategoryDTO>> response =
        restTemplate.exchange(
            CATEGORY_URI,
            HttpMethod.GET,
            ResponseEntity.EMPTY,
            new ParameterizedTypeReference<List<CategoryDTO>>() {});

    assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    assertEquals(NUMBER_OF_CATEGORIES, response.getBody().size());
  }

  @Test
  public void testGetAllCategoriesEmpty() {

    ResponseEntity<List<CategoryDTO>> response =
        restTemplate.exchange(
            CATEGORY_URI,
            HttpMethod.GET,
            ResponseEntity.EMPTY,
            new ParameterizedTypeReference<List<CategoryDTO>>() {});

    assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    assertEquals(0, response.getBody().size());
  }

  private void createCategories(int numberOfCategories) {
    for (int i = 0; i < numberOfCategories; i++) {
      createCategory("Category " + i);
    }
  }

  private Category createCategory(String name) {
    if (categoryRepository.existsByName(name)) {
      return categoryRepository.findByName(name).orElse(null);
    } else {
      Category category = Category.builder().name(name).build();
      return categoryRepository.save(category);
    }
  }
}
