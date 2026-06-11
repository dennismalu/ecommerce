package it.progetto.ecommerce.services.carrello;

import it.progetto.ecommerce.model.dto.CarrelloProductDTO;
import it.progetto.ecommerce.model.entities.CarrelloProductEntity;
import it.progetto.ecommerce.model.exceptions.DifferentUserException;
import it.progetto.ecommerce.model.exceptions.ProductNotFoundException;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CarrelloService {

    List<CarrelloProductDTO> getCarrello();
    void addToCarrello(long idProdotto) throws ProductNotFoundException;
    void updateCarrello(long idProdottoCarrello, int quantity) throws DifferentUserException, ProductNotFoundException;
    void removeFromCarrello(long idProdottoCarrello) throws DifferentUserException, ProductNotFoundException ;
}
