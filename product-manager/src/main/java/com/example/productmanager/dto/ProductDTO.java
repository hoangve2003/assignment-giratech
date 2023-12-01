package com.example.productmanager.dto;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {
    private UUID idProduct;
    private String name;
    private String description;
    private Double price;
    @Temporal(TemporalType.DATE)
    private Date createdAt = new Date();
    @Temporal(TemporalType.DATE)
    private Date updatedAt;
    private Boolean available;
    private UUID idCategory;
    private String categoryName;
}
