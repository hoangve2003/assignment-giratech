package com.example.productmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@Entity
@Table(name = "Category")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Category implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;
    private String description;
    @Temporal(TemporalType.DATE)
    private Date createdAt;
    @Temporal(TemporalType.DATE)
    private Date updatedAt;
    private Boolean active;
    @JsonIgnore
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> productList;


    @Override
    public Object clone() {
        try {
            Category clonedCategory = (Category) super.clone();
            if (this.productList != null) {
                List<Product> clonedProductList = new ArrayList<>();
                for (Product product : this.productList) {
                    clonedProductList.add((Product) product.clone());
                }
                clonedCategory.setProductList(clonedProductList);
            }

            return clonedCategory;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return "Not found";
    }
}
