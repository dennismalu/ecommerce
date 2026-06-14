package it.progetto.ecommerce.services.transactional;

import it.progetto.ecommerce.model.entities.OrderEntity;
import it.progetto.ecommerce.model.entities.UserEntity;
import it.progetto.ecommerce.model.exceptions.CustomException;

public interface TransactionalService {
    void ordineTransaction(OrderEntity orderEntity, UserEntity userEntity);
}
