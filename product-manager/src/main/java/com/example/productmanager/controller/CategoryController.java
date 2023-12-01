package com.example.productmanager.controller;

import com.example.productmanager.dto.CategoryDTO;
import com.example.productmanager.exception.CategoryNotFoundException;
import com.example.productmanager.model.Category;
import com.example.productmanager.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.UUID;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;

    @GetMapping("/all")
    public ResponseEntity<Page<Category>> paginationCategories(@RequestParam(defaultValue = "0") Integer page) {
        return service.paginationCategories(page, 5);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable("id") UUID id) {
        return service.getCategoryById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<Category> addCategory(@RequestBody @Valid CategoryDTO categoryDTO) {
        return service.addCategory(categoryDTO);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable("id") UUID id, @RequestBody @Valid CategoryDTO categoryDTO) throws CategoryNotFoundException, ParseException {
        return service.updateCategory(id, categoryDTO);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable("id") UUID id) throws CategoryNotFoundException {
        return service.deleteCategory(id);
    }
}
