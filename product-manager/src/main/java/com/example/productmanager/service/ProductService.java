package com.example.productmanager.service;

import com.example.productmanager.dto.ProductDTO;
import com.example.productmanager.exception.ExistProductNameException;
import com.example.productmanager.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface ProductService {
    ResponseEntity<Page<Product>> paginationProducts(Integer page, Integer pageSize);

    ResponseEntity<Product> addProduct(ProductDTO productDTO) throws ExistProductNameException;

    ResponseEntity<Product> updateProduct(UUID id, ProductDTO productDTO);

    ResponseEntity<String> deleteProduct(UUID id);

    ResponseEntity<Page<Product>> sortProductByPriceDesc(int pageNumber, int pageSize);

    ResponseEntity<Product> findByProductName(String productName);
}
