package it.progetto.ecommerce.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserDashboardDTO {
    private List<CarrelloProductDTO> carrello;

    private String lastSearch;
    private List<ProductDTO> productsLashSearch;

    private List<ProductDTO> lastProductOpened;

    private List<ListaDesideriProductDTO> listaDesideri;
}
