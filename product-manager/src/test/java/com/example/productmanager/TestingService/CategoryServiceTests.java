package com.example.productmanager.TestingService;


import com.example.productmanager.dto.CategoryDTO;
import com.example.productmanager.exception.CategoryExistException;
import com.example.productmanager.exception.CategoryNotFoundException;
import com.example.productmanager.model.Category;
import com.example.productmanager.repository.CategoryRepository;
import com.example.productmanager.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

@SpringBootTest
//@RequiredArgsConstructor
class CategoryServiceTests {

    @Autowired
    private CategoryServiceImpl categoryService;

    @MockBean
    private CategoryRepository categoryRepository;


    @Test
    void testPaginationCategories() {
        int pageNumber = 0;
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(Category.builder().id(UUID.randomUUID()).name("name").description("des").createdAt(new Date()).active(true).build());

        Page<Category> categoryPage = new PageImpl<>(categoryList);

        Mockito.when(categoryRepository.getAllCategories(pageable)).thenReturn(categoryPage);

        ResponseEntity<Page<Category>> response = categoryService.paginationCategories(pageNumber, pageSize);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(categoryPage, response.getBody());
    }

    @Test
    void testPaginationCategories_ReturnNotFound() {
        int pageNumber = 0;
        int pageSize = 5;
        List<Category> categoryListEmpty = new ArrayList<>();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Category> emptyCategoryPage = new PageImpl<>(categoryListEmpty, pageable, categoryListEmpty.size());

        Mockito.when(categoryRepository.getAllCategories(pageable)).thenReturn(emptyCategoryPage);

        ResponseEntity<Page<Category>> response = categoryService.paginationCategories(pageNumber, pageSize);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals(emptyCategoryPage, response.getBody());

        Mockito.verify(categoryRepository, Mockito.times(1)).getAllCategories(pageable);
    }


    @Test
    void testUpdateCategory() {
        UUID categoryId = UUID.randomUUID();
        Category category = Category.builder().id(categoryId).name("Old").description("Old").createdAt(new Date()).active(true).build();
        CategoryDTO categoryDTO = CategoryDTO.builder().id(categoryId).name("New").description("New").createdAt(new Date()).active(true).build();


        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        ResponseEntity<Category> response = categoryService.updateCategory(categoryId, categoryDTO);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(categoryDTO.getName(), response.getBody().getName());
        Assertions.assertEquals(categoryDTO.getDescription(), response.getBody().getDescription());
        Mockito.verify(categoryRepository).save(category);
    }

    @Test
    void testUpdateCategory_RetunNotFound() {
        UUID categoryId = UUID.randomUUID();
        CategoryDTO categoryDTO = CategoryDTO.builder().id(categoryId).name("New").description("New").createdAt(new Date()).active(true).build();

        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Assertions.assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(categoryId, categoryDTO));
    }


    @Test
    void testDeleteCategory() throws CloneNotSupportedException {
        UUID id = UUID.randomUUID();

        Category category = Category.builder().id(id).build();

        Mockito.when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        ResponseEntity<String> response = categoryService.deleteCategory(id);

        Mockito.verify(categoryRepository, Mockito.times(1)).deleteById(id);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteCategory_CategoryNotFound_ReturnsNotFound() {
        UUID categoryId = UUID.randomUUID();
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        Assertions.assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(categoryId));
        Mockito.verify(categoryRepository, Mockito.never()).deleteById(categoryId);
    }


    @Test
    void testAddCategory_ReturnsCreated() {
        CategoryDTO categoryDTO = CategoryDTO.builder().name("name").description("description").createdAt(new Date()).build();
        Category category = Category.builder()
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .createdAt(categoryDTO.getCreatedAt())
                .build();

        Mockito.when(categoryRepository.existsByName(categoryDTO.getName())).thenReturn(false);
        ResponseEntity<Category> response = categoryService.addCategory(categoryDTO);
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(categoryDTO.getName(), response.getBody().getName());

        Mockito.verify(categoryRepository, Mockito.times(1)).save(category);
    }

    @Test
    void testAddCategory_CategoryAlreadyExists() {
        CategoryDTO categoryDTO = CategoryDTO.builder().name("name").build();
        Category category = Category.builder().name(categoryDTO.getName()).build();

        Mockito.when(categoryRepository.existsByName(categoryDTO.getName())).thenReturn(true);

        CategoryExistException exception = Assertions.assertThrows(CategoryExistException.class, () -> categoryService.addCategory(categoryDTO));

        Assertions.assertEquals("Category name was existed: " + categoryDTO.getName(), exception.getMessage());

        Mockito.verify(categoryRepository, Mockito.never()).save(category);
    }


    @Test
    void testGetCategoryById_ReturnsCategory() throws CloneNotSupportedException {
        UUID categoryId = UUID.randomUUID();
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Test Category");
        category.setDescription("Test Description");

        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        ResponseEntity<Category> response = categoryService.getCategoryById(categoryId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(category, response.getBody());

        Mockito.verify(categoryRepository, Mockito.times(1)).findById(categoryId);
    }

    @Test
    void testGetCategoryById_CategoryDoesNotExist() {
        UUID categoryId = UUID.randomUUID();

        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Assertions.assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.getCategoryById(categoryId);
        });

        Mockito.verify(categoryRepository, Mockito.times(1)).findById(categoryId);
    }


}




