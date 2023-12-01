package com.example.productmanager.service;

import com.example.productmanager.dto.CategoryDTO;
import com.example.productmanager.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.UUID;

public interface CategoryService {

    ResponseEntity<Page<Category>> paginationCategories(Integer pageNumber, Integer pageSize);

    ResponseEntity<Category> addCategory(CategoryDTO categoryDTO);

    ResponseEntity<Category> updateCategory(UUID id, CategoryDTO categoryDTO) throws ParseException;

    ResponseEntity<String> deleteCategory(UUID id);

    ResponseEntity<Category> getCategoryById(UUID id);
}
