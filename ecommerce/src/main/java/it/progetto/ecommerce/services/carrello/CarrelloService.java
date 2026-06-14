package it.progetto.ecommerce.services.carrello;

import it.progetto.ecommerce.model.dto.CarrelloProductDTO;
import it.progetto.ecommerce.model.dto.OrderDTO;
import it.progetto.ecommerce.model.entities.CarrelloProductEntity;
import it.progetto.ecommerce.model.exceptions.CustomException;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CarrelloService {

    List<CarrelloProductDTO> getCarrello();
    void addToCarrello(long idProdotto) throws CustomException;
    void updateCarrello(long idProdottoCarrello, int quantity) throws CustomException;
    void removeFromCarrello(long idProdottoCarrello) throws CustomException;

    void orderCarrello(OrderDTO orderDTO) throws CustomException;
}
