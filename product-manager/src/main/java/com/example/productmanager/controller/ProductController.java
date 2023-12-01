package com.example.productmanager.controller;

import com.example.productmanager.dto.ProductDTO;
import com.example.productmanager.exception.ProductNotFoundException;
import com.example.productmanager.model.Product;
import com.example.productmanager.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @GetMapping("/all")
    public ResponseEntity<Page<Product>> paginationCategories(@RequestParam(defaultValue = "0") Integer page) {
        return service.paginationProducts(page, 5);
    }

    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@RequestBody @Valid ProductDTO productDTO) {
        return service.addProduct(productDTO);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") UUID id, @RequestBody @Valid ProductDTO productDTO) throws ProductNotFoundException {
        return service.updateProduct(id, productDTO);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") UUID id) throws ProductNotFoundException {
        return service.deleteProduct(id);
    }

    @GetMapping("/sort-by-price")
    public ResponseEntity<Page<Product>> sortByPrice(@RequestParam(defaultValue = "0") Integer page) {
        return service.sortProductByPriceDesc(page, 5);
    }

    @GetMapping("/find-by-name")
    public ResponseEntity<Product> findByName(@RequestParam String name) {
        return service.findByProductName(name);
    }
}
