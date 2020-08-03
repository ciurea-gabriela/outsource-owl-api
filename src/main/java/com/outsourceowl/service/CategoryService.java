package com.outsourceowl.service;

import com.outsourceowl.dto.CategoryDTO;
import com.outsourceowl.exception.ResourceNotFoundException;
import com.outsourceowl.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
  private final CategoryRepository categoryRepository;
  private final ModelMapper modelMapper;

  @Autowired
  public CategoryService(CategoryRepository categoryRepository, ModelMapper modelMapper) {
    this.categoryRepository = categoryRepository;
    this.modelMapper = modelMapper;
  }

  public List<CategoryDTO> getAllCategories() {
    return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
        .map(c -> modelMapper.map(c, CategoryDTO.class))
        .collect(Collectors.toList());
  }

  public CategoryDTO getCategoryById(Long categoryId) {
    return categoryRepository
        .findById(categoryId)
        .map(c -> modelMapper.map(c, CategoryDTO.class))
        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
  }
}
