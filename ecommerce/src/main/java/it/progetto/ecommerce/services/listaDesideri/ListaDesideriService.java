package it.progetto.ecommerce.services.listaDesideri;

import it.progetto.ecommerce.model.dto.CarrelloProductDTO;
import it.progetto.ecommerce.model.dto.ListaDesideriProductDTO;
import it.progetto.ecommerce.model.exceptions.DifferentUserException;
import it.progetto.ecommerce.model.exceptions.ProductNotFoundException;

import java.util.List;

public interface ListaDesideriService {

    List<ListaDesideriProductDTO> getListaDesideri();
    void addToListaDesideri(long idProdotto) throws ProductNotFoundException;
    void removeFromListaDesideri(long idProdottoListaDesideri) throws DifferentUserException, ProductNotFoundException ;


}
