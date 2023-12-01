package com.example.productmanager.repository;

import com.example.productmanager.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("SELECT p FROM Product p where p.available = true")
    Page<Product> getAllProducts(Pageable pageable);

    boolean existsByName(String name);

    @Query("SELECT p FROM Product p where p.available = true and p.name= ?1")
    Product findByName(String productName);


}
