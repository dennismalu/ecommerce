package it.progetto.ecommerce.services.listaDesideri;

import it.progetto.ecommerce.model.dto.CarrelloProductDTO;
import it.progetto.ecommerce.model.dto.ListaDesideriProductDTO;
import it.progetto.ecommerce.model.exceptions.CustomException;

import java.util.List;

public interface ListaDesideriService {

    List<ListaDesideriProductDTO> getListaDesideri();
    void addToListaDesideri(long idProdotto) throws CustomException;
    void removeFromListaDesideri(long idProdottoListaDesideri) throws CustomException;


}
