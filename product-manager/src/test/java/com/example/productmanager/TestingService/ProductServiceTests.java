package com.example.productmanager.TestingService;

import com.example.productmanager.dto.ProductDTO;
import com.example.productmanager.exception.CategoryNotFoundException;
import com.example.productmanager.exception.ExistProductNameException;
import com.example.productmanager.exception.ProductNotFoundException;
import com.example.productmanager.model.Category;
import com.example.productmanager.model.Product;
import com.example.productmanager.repository.CategoryRepository;
import com.example.productmanager.repository.ProductRepository;
import com.example.productmanager.service.impl.CategoryServiceImpl;
import com.example.productmanager.service.impl.ProductServiceImpl;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
class ProductServiceTests {

    @MockBean
    ProductRepository productRepository;
    @MockBean
    CategoryRepository categoryRepository;

    @Autowired
    ProductServiceImpl productService;

    @Autowired
    CategoryServiceImpl categoryService;

    @Test
    void paginationProductsTest() {
        int pageNumber = 0;
        int pageSize = 5;
        List<Product> productList = new ArrayList<>();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        productList.add(Product.builder().name("Product 1").description("Description 1").price(100.0).build());

        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());

        Mockito.when(productRepository.getAllProducts(pageable)).thenReturn(productPage);

        ResponseEntity<Page<Product>> response = productService.paginationProducts(pageNumber, pageSize);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(productPage, response.getBody());

        Mockito.verify(productRepository, Mockito.times(1)).getAllProducts(pageable);
    }

    @Test
    void paginationProductsTest_ReturnNotFound() {
        int pageNumber = 0;
        int pageSize = 5;
        List<Product> listEmpty = new ArrayList<>();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Product> pageEmpty = new PageImpl<>(listEmpty, pageable, listEmpty.size());

        Mockito.when(productRepository.getAllProducts(pageable)).thenReturn(pageEmpty);

        ResponseEntity<Page<Product>> response = productService.paginationProducts(pageNumber, pageSize);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals(pageEmpty, response.getBody());

        Mockito.verify(productRepository, Mockito.times(1)).getAllProducts(pageable);
    }

    @Test
    void addProductTest() {
        ProductDTO productDTO = ProductDTO.builder()
                .idProduct(UUID.randomUUID())
                .name("name")
                .description("description")
                .idCategory(UUID.randomUUID()).build();

        Product product = Product.builder()
                .id(productDTO.getIdProduct())
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .category(Category.builder().id(productDTO.getIdCategory()).build())
                .build();


        Mockito.when(productRepository.existsByName(productDTO.getName())).thenReturn(false);
        Mockito.when(categoryRepository.existsById(product.getCategory().getId())).thenReturn(true);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        ResponseEntity<Product> response = productService.addProduct(productDTO);

        // Kiểm tra kết quả trả về
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(product, response.getBody());

        Mockito.verify(productRepository, Mockito.times(1)).existsByName(productDTO.getName());
        Mockito.verify(categoryRepository, Mockito.times(1)).existsById(product.getCategory().getId());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
    }

    @Test
    void addProductTest_ExistByName() {
        ProductDTO productDTO = ProductDTO.builder()
                .idProduct(UUID.randomUUID())
                .name("name")
                .description("description")
                .idCategory(UUID.randomUUID()).build();

        Product product = Product.builder()
                .id(productDTO.getIdProduct())
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .category(Category.builder().id(productDTO.getIdCategory()).build())
                .build();


        Mockito.when(productRepository.existsByName(product.getName())).thenReturn(true);
        Mockito.when(categoryRepository.existsById(product.getCategory().getId())).thenReturn(true);

        Assertions.assertThrows(ExistProductNameException.class, () -> productService.addProduct(productDTO));
    }

    @Test
    void addProduct_InvalidCategoryId() {

        ProductDTO productDTO = ProductDTO.builder()
                .idProduct(UUID.randomUUID())
                .name("name")
                .description("description")
                .idCategory(UUID.randomUUID()).build();

        Product product = Product.builder()
                .id(productDTO.getIdProduct())
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .category(Category.builder().id(productDTO.getIdCategory()).build())
                .build();

        Mockito.when(productRepository.existsByName(product.getName())).thenReturn(false);
        Mockito.when(categoryRepository.existsById(product.getCategory().getId())).thenReturn(false);

        Assertions.assertThrows(CategoryNotFoundException.class, () -> productService.addProduct(productDTO));
    }

    @Test
    void updateProductTest() {
        UUID productId = UUID.randomUUID();

        ProductDTO productDTO = ProductDTO.builder()
                .idProduct(productId)
                .name("name updated")
                .description("description")
                .idCategory(UUID.randomUUID()).build();

        Product product = Product.builder()
                .id(productDTO.getIdProduct())
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .category(Category.builder().id(productDTO.getIdCategory()).build())
                .build();

        Product existingProduct = Product.builder()
                .id(productId)
                .name("Old name")
                .description("Old des")
                .category(Category.builder().id(UUID.randomUUID()).build())
                .build();


        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        Mockito.when(productRepository.save(product)).thenAnswer(invocation -> invocation.getArgument(0));

        // Gọi phương thức updateProduct
        ResponseEntity<Product> response = productService.updateProduct(productId, productDTO);

        // Kiểm tra kết quả trả về
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(productId, response.getBody().getId());
        Assertions.assertEquals(productDTO.getName(), response.getBody().getName());
    }

    @Test
    void updateProductTest_ProductNotFound() {
        UUID productId = UUID.randomUUID();

        ProductDTO productDTO = ProductDTO.builder()
                .idProduct(productId)
                .name("name updated")
                .description("description")
                .idCategory(UUID.randomUUID()).build();
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(productId, productDTO));
    }

    @Test
    void deleteProductTest() {
        UUID productId = UUID.randomUUID();

        ProductDTO dto = ProductDTO.builder().idProduct(productId).name("name").available(false).build();
        Product existingProduct = Product.builder().id(dto.getIdProduct()).name(dto.getName()).available(dto.getAvailable()).build();

        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        Mockito.when(productRepository.save(existingProduct)).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<String> response = productService.deleteProduct(productId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Deleted product", response.getBody());


        Assertions.assertFalse(existingProduct.getAvailable());

        Mockito.verify(productRepository, Mockito.times(1)).findById(productId);
        Mockito.verify(productRepository, Mockito.times(1)).save(existingProduct);
    }

    @Test
    void deleteProduct_ProductNotFound_ThrowsProductNotFoundException() {
        UUID productId = UUID.randomUUID();

        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Kiểm tra ngoại lệ
        Assertions.assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(productId));

        Mockito.verify(productRepository, Mockito.times(1)).findById(productId);
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any(Product.class));
    }

}
