package it.progetto.ecommerce.services.carrello;

import it.progetto.ecommerce.model.dto.CarrelloProductDTO;
import it.progetto.ecommerce.model.entities.CarrelloProductEntity;
import it.progetto.ecommerce.model.entities.ProductEntity;
import it.progetto.ecommerce.model.entities.UserDetailsEntity;
import it.progetto.ecommerce.model.entities.UserEntity;
import it.progetto.ecommerce.model.exceptions.DifferentUserException;
import it.progetto.ecommerce.model.exceptions.ProductNotFoundException;
import it.progetto.ecommerce.model.mapper.CarrelloProductMapper;
import it.progetto.ecommerce.model.mapper.UserMapper;
import it.progetto.ecommerce.repository.CarrelloProductRepository;
import it.progetto.ecommerce.repository.ProductRepository;
import it.progetto.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarrelloServiceImpl implements CarrelloService {

    private final UserRepository userRepository;
    private final CarrelloProductRepository carrelloProductRepository;
    private final CarrelloProductMapper carrelloProductMapper;
    private final ProductRepository productRepository;
    private final UserMapper userMapper;

    @Override
    public List<CarrelloProductDTO> getCarrello() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsEntity userDetailsEntity = (UserDetailsEntity) auth.getPrincipal();

        List<CarrelloProductEntity> carrello = carrelloProductRepository.getAllByUser_IdOrderByProduct_Name(userDetailsEntity.getId());

        List<CarrelloProductDTO> carrelloDTO = new LinkedList<>();
        for(CarrelloProductEntity carrelloProduct : carrello) {
            carrelloDTO.add(carrelloProductMapper.toDto(carrelloProduct));
        }
        return carrelloDTO;
    }

    @Override
    public void addToCarrello(long idProdotto) throws ProductNotFoundException {
        ProductEntity productEntity = productRepository.findFirstById(idProdotto);
        if(productEntity == null) {
            throw new ProductNotFoundException();
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsEntity userDetailsEntity = (UserDetailsEntity) auth.getPrincipal();
        UserEntity userEntity = userMapper.toUserEntity(userDetailsEntity);

        CarrelloProductEntity carrelloProductEntity = new CarrelloProductEntity();
        carrelloProductEntity.setQuantity(1);
        carrelloProductEntity.setUser(userEntity);
        carrelloProductEntity.setProduct(productEntity);

        carrelloProductRepository.save(carrelloProductEntity);
    }

    @Override
    public void updateCarrello(long idProdottoCarrello, int quantity) throws DifferentUserException, ProductNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsEntity userDetailsEntity = (UserDetailsEntity) auth.getPrincipal();

        CarrelloProductEntity carrelloProductEntity = carrelloProductRepository.findFirstById(idProdottoCarrello);

        if(carrelloProductEntity == null) {
            throw new ProductNotFoundException();
        }
        else if(!carrelloProductEntity.getUser().getId().equals(userDetailsEntity.getId())) {
            throw new DifferentUserException();
        }

        carrelloProductEntity.setQuantity(quantity);
        carrelloProductRepository.save(carrelloProductEntity);
    }

    @Override
    public void removeFromCarrello(long idProdottoCarrello) throws DifferentUserException, ProductNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsEntity userDetailsEntity = (UserDetailsEntity) auth.getPrincipal();

        CarrelloProductEntity carrelloProductEntity = carrelloProductRepository.findFirstById(idProdottoCarrello);

        if(carrelloProductEntity == null) {
            throw new ProductNotFoundException();
        }
        else if(!carrelloProductEntity.getUser().getId().equals(userDetailsEntity.getId())) {
            throw new DifferentUserException();
        }

        carrelloProductRepository.delete(carrelloProductEntity);
    }

}
