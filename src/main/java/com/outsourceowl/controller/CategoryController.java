package com.outsourceowl.controller;

import com.outsourceowl.dto.CategoryDTO;
import com.outsourceowl.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {
  private final CategoryService categoryService;

  @Autowired
  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @GetMapping("/categories")
  public List<CategoryDTO> getAllCategories() {
    return categoryService.getAllCategories();
  }

  @GetMapping("/categories/{categoryId}")
  public CategoryDTO getCategory(@PathVariable Long categoryId) {
    return categoryService.getCategoryById(categoryId);
  }
}
