package com.example.productmanager.repository;

import com.example.productmanager.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    @Query("SELECT c FROM Category c where c.active = true")
    Page<Category> getAllCategories(Pageable pageable);

    Boolean existsByName(String name);
}
