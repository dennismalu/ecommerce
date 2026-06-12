package it.progetto.ecommerce.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ListaDesideriProductDTO {
    private Long id;

    private ProductDTO product;
}