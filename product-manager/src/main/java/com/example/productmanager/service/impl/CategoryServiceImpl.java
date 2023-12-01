/**
 * Implementation of the CategoryService interface that provides CRUD operations for categories.
 */
package com.example.productmanager.service.impl;

import com.example.productmanager.dto.CategoryDTO;
import com.example.productmanager.exception.CategoryExistException;
import com.example.productmanager.exception.CategoryNotFoundException;
import com.example.productmanager.model.Category;
import com.example.productmanager.repository.CategoryRepository;
import com.example.productmanager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private Category category = new Category();
    private Category clone = (Category) category.clone();

    /**
     * Retrieve a paginated list of categories.
     *
     * @param pageNumber the page number to retrieve
     * @param pageSize   the number of categories per page
     * @return a ResponseEntity containing a Page of Category objects
     */
    public ResponseEntity<Page<Category>> paginationCategories(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Category> categoryPage = categoryRepository.getAllCategories(pageable);
        if (categoryPage.isEmpty()) {
            return new ResponseEntity<>(categoryPage, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(categoryPage, HttpStatus.OK);
        }
    }

    /**
     * Update a category with the given id.
     *
     * @param id          the id of the category to update
     * @param categoryDTO the CategoryDTO object containing the updated information
     * @return a ResponseEntity containing the updated Category object
     * @throws CategoryNotFoundException if the category with the given id is not found
     */
    @Override
    public ResponseEntity<Category> updateCategory(UUID id, CategoryDTO categoryDTO) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isPresent()) {
            clone = categoryOptional.get();
            if (categoryDTO.getName() != null) {
                clone.setName(categoryDTO.getName());
            }
            if (categoryDTO.getDescription() != null) {
                clone.setDescription(categoryDTO.getDescription());
            }
            clone.setUpdatedAt(new Date());
            categoryRepository.save(clone);
        } else {
            throw new CategoryNotFoundException("Category not found with id: " + id);
        }
        return new ResponseEntity<>(clone, HttpStatus.OK);
    }

    /**
     * Delete a category with the given id.
     *
     * @param id the id of the category to delete
     * @return a ResponseEntity with no content
     */
    @Override
    public ResponseEntity<String> deleteCategory(UUID id) {
        getCategoryById(id);
        categoryRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Retrieve a category by its id.
     *
     * @param id the id of the category to retrieve
     * @return a ResponseEntity containing the retrieved Category object
     */
    @Override
    public ResponseEntity<Category> getCategoryById(UUID id) {
        clone = categoryRepository.findById(id).orElseThrow(
                () -> new CategoryNotFoundException("Category not found with id: " + id)
        );
        return new ResponseEntity<>(clone, HttpStatus.OK);
    }

    /**
     * Add a new category.
     *
     * @param categoryDTO the CategoryDTO object containing the information of the new category
     * @return a ResponseEntity containing the created Category object
     * @throws CategoryExistException if a category with the same name already exists
     */
    @Override
    public ResponseEntity<Category> addCategory(CategoryDTO categoryDTO) {
        clone = convertToObject(categoryDTO);
        if (Boolean.TRUE.equals(categoryRepository.existsByName(clone.getName()))) {
            throw new CategoryExistException("Category name already exists: " + clone.getName());
        }
        categoryRepository.save(clone);
        return new ResponseEntity<>(clone, HttpStatus.CREATED);
    }

    /**
     * Map a CategoryDTO object to a Category object.
     *
     * @param categoryDTO the CategoryDTO object to map
     * @return the mapped Category object
     */
    private Category convertToObject(CategoryDTO categoryDTO) {
        return Category.builder()
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .createdAt(categoryDTO.getCreatedAt())
                .active(categoryDTO.getActive())
                .build();
    }

}