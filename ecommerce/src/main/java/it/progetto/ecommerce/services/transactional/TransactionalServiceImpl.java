package it.progetto.ecommerce.services.transactional;

import it.progetto.ecommerce.model.entities.OrderEntity;
import it.progetto.ecommerce.model.entities.UserEntity;
import it.progetto.ecommerce.model.exceptions.CustomException;
import it.progetto.ecommerce.model.mapper.CarrelloProductMapper;
import it.progetto.ecommerce.model.mapper.UserMapper;
import it.progetto.ecommerce.repository.CarrelloProductRepository;
import it.progetto.ecommerce.repository.OrderRepository;
import it.progetto.ecommerce.repository.ProductRepository;
import it.progetto.ecommerce.repository.UserRepository;
import it.progetto.ecommerce.services.carrello.CarrelloService;
import it.progetto.ecommerce.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionalServiceImpl implements TransactionalService {

    private final CarrelloProductRepository carrelloProductRepository;
    private final CarrelloProductMapper carrelloProductMapper;
    private final ProductRepository productRepository;
    private final UserMapper userMapper;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final UserService userService;


    @Override
    @Transactional
    public void ordineTransaction(OrderEntity orderEntity, UserEntity userEntity) {
        double updatedUserPortafoglio = userEntity.getPortafoglio() - orderEntity.getPrice();
        userRepository.updatePortafoglioById(userEntity.getId(), updatedUserPortafoglio);

        double updatedAdminPortafoglio = userService.getAdminUserEntity().getPortafoglio() + orderEntity.getPrice();
        userRepository.updatePortafoglioById(userService.getAdminUserEntity().getId(), updatedAdminPortafoglio);

        orderRepository.save(orderEntity);
    }
}
