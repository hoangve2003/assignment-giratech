package com.example.productmanager.service.impl;

import com.example.productmanager.dto.ProductDTO;
import com.example.productmanager.exception.CategoryNotFoundException;
import com.example.productmanager.exception.ExistProductNameException;
import com.example.productmanager.exception.ProductNotFoundException;
import com.example.productmanager.model.Category;
import com.example.productmanager.model.Product;
import com.example.productmanager.repository.CategoryRepository;
import com.example.productmanager.repository.ProductRepository;
import com.example.productmanager.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;

    /**
     * Retrieve a paginated list of products.
     *
     * @param pageNumber the page number to retrieve
     * @param pageSize   the number of products per page
     * @return a ResponseEntity containing a Page of Product objects
     */
    @Override
    public ResponseEntity<Page<Product>> paginationProducts(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Product> products = repository.getAllProducts(pageable);
        if (products.isEmpty()) {
            return new ResponseEntity<>(products, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(products, HttpStatus.OK);
        }
    }

    /**
     * Add a new product.
     *
     * @param productDTO the ProductDTO object containing the information of the new product
     * @return a ResponseEntity containing the created Product object
     * @throws ExistProductNameException if a product with the same name already exists
     * @throws CategoryNotFoundException if the category with the given id is not found
     */
    @Override
    public ResponseEntity<Product> addProduct(ProductDTO productDTO) {
        Product product = convertToObject(productDTO);
        UUID idCategory = product.getCategory().getId();
        if (repository.existsByName(product.getName())) {
            throw new ExistProductNameException("Product name already exists: " + product.getName());
        }
        if (idCategory != null && !categoryRepository.existsById(idCategory)) {
            throw new CategoryNotFoundException("Invalid categoryId: " + idCategory);
        }
        repository.save(product);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    /**
     * Convert a ProductDTO object to a Product object.
     *
     * @param productDTO the ProductDTO object to convert
     * @return the converted Product object
     */
    private Product convertToObject(ProductDTO productDTO) {
        return Product.builder()
                .id(productDTO.getIdProduct())
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .createdAt(productDTO.getCreatedAt())
                .available(productDTO.getAvailable())
                .category(Category.builder()
                        .id(productDTO.getIdCategory())
                        .build())
                .build();
    }

    /**
     * Update a product with the given id.
     *
     * @param id         the id of the product to update
     * @param productDTO the ProductDTO object containing the updated information
     * @return a ResponseEntity containing the updated Product object
     * @throws ProductNotFoundException if the product with the given id is not found
     */
    @Override
    public ResponseEntity<Product> updateProduct(UUID id, ProductDTO productDTO) {
        Optional<Product> productOptional = repository.findById(id);
        Product product;
        if (productOptional.isPresent()) {
            product = productOptional.get();
            if (product.getName() != null) {
                product.setName(productDTO.getName());
            }
            if (product.getDescription() != null) {
                product.setDescription(productDTO.getDescription());
            }
            if (product.getPrice() != null) {
                product.setPrice(productDTO.getPrice());
            }
            product.setUpdatedAt(new Date());
            product.setAvailable(true);
            if (product.getCategory().getId() != null) {
                product.getCategory().setId(productDTO.getIdCategory());
            }
            repository.save(product);
        } else {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    /**
     * Delete a product with the given id.
     *
     * @param id the id of the product to delete
     * @return a ResponseEntity with a message indicating the product has been deleted
     * @throws ProductNotFoundException if the product with the given id is not found
     */
    @Override
    public ResponseEntity<String> deleteProduct(UUID id) {
        Optional<Product> productOptional = repository.findById(id);
        Product product;
        if (productOptional.isPresent()) {
            product = productOptional.get();
            if (product.getAvailable() != null) {
                product.setAvailable(false);
            }
            repository.save(product);
        } else {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        return new ResponseEntity<>("Deleted product", HttpStatus.OK);
    }

    /**
     * Retrieve a paginated list of products sorted by price in descending order.
     *
     * @param pageNumber the page number to retrieve
     * @param pageSize   the number of products per page
     * @return a ResponseEntity containing a Page of Product objects sorted by price in descending order
     */
    public ResponseEntity<Page<Product>> sortProductByPriceDesc(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("price").descending());
        Page<Product> products = repository.getAllProducts(pageable);
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(products, HttpStatus.OK);
        }
    }

    /**
     * Find a product by its name.
     *
     * @param productName the name of the product to find
     * @return a ResponseEntity containing the retrieved Product object
     * @throws ExistProductNameException if the product with the given name is not found
     */
    public ResponseEntity<Product> findByProductName(String productName) {
        if (!repository.existsByName(productName)) {
            throw new ExistProductNameException("Product not found: " + productName);
        }
        return new ResponseEntity<>(repository.findByName(productName), HttpStatus.OK);
    }


}
