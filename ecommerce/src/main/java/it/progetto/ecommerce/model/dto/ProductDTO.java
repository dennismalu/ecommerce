package it.progetto.ecommerce.model.dto;

import it.progetto.ecommerce.model.entities.CategoryEntity;
import lombok.Data;

@Data
public class ProductDTO {
    private Long id;

    private String name;

    private String description;

    private Double price;

    private Boolean disabled;

    private byte[] image;

    private Long categoryId;

    private String categoryName;
}
