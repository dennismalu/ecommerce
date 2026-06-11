package it.progetto.ecommerce.model.dto;

import it.progetto.ecommerce.model.entities.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class CarrelloProductDTO {
    private Long id;

    private Integer quantity;

    private ProductDTO product;
}


