package it.progetto.ecommerce.services.carrello;

import it.progetto.ecommerce.model.dto.CarrelloProductDTO;
import it.progetto.ecommerce.model.entities.CarrelloProductEntity;
import it.progetto.ecommerce.model.entities.ProductEntity;
import it.progetto.ecommerce.model.entities.UserDetailsEntity;
import it.progetto.ecommerce.model.entities.UserEntity;
import it.progetto.ecommerce.model.exceptions.CustomExceptionBuilder;
import it.progetto.ecommerce.model.exceptions.CustomException;
import it.progetto.ecommerce.model.mapper.CarrelloProductMapper;
import it.progetto.ecommerce.model.mapper.UserMapper;
import it.progetto.ecommerce.repository.CarrelloProductRepository;
import it.progetto.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarrelloServiceImpl implements CarrelloService {

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
    public void addToCarrello(long idProdotto) throws CustomException {
        ProductEntity productEntity = productRepository.findFirstById(idProdotto);
        if(productEntity == null) {
            throw CustomExceptionBuilder.productNotFound(); //errore 404
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsEntity userDetailsEntity = (UserDetailsEntity) auth.getPrincipal();
        UserEntity userEntity = userMapper.toUserEntity(userDetailsEntity);

        CarrelloProductEntity carrelloProductEntity = new CarrelloProductEntity();
        carrelloProductEntity.setQuantity(1);
        carrelloProductEntity.setUser(userEntity);
        carrelloProductEntity.setProduct(productEntity);

        try {
            carrelloProductRepository.save(carrelloProductEntity);
        } catch(Exception e){
            throw CustomExceptionBuilder.productNotAddedToCart();
        }
    }

    @Override
    public void updateCarrello(long idProdottoCarrello, int quantity) throws CustomException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsEntity userDetailsEntity = (UserDetailsEntity) auth.getPrincipal();

        CarrelloProductEntity carrelloProductEntity = carrelloProductRepository.findFirstById(idProdottoCarrello);

        if(carrelloProductEntity == null) {
            throw CustomExceptionBuilder.productNotFound(); //errore 404
        }
        else if(!carrelloProductEntity.getUser().getId().equals(userDetailsEntity.getId())) {
            throw CustomExceptionBuilder.userNotAllowed(); //errore 405
        }

        carrelloProductEntity.setQuantity(quantity);
        try {
            carrelloProductRepository.save(carrelloProductEntity);
        } catch(Exception e){
            throw CustomExceptionBuilder.cartNotUpdated(); //errore 500
        }
    }

    @Override
    public void removeFromCarrello(long idProdottoCarrello) throws CustomException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsEntity userDetailsEntity = (UserDetailsEntity) auth.getPrincipal();

        CarrelloProductEntity carrelloProductEntity = carrelloProductRepository.findFirstById(idProdottoCarrello);

        if(carrelloProductEntity == null) {
            throw CustomExceptionBuilder.productNotFound(); //errore 404
        }
        else if(!carrelloProductEntity.getUser().getId().equals(userDetailsEntity.getId())) {
            throw CustomExceptionBuilder.userNotAllowed(); //errore 405
        }

        try {
            carrelloProductRepository.delete(carrelloProductEntity);
        } catch(Exception e){
            throw CustomExceptionBuilder.productNotRemovedFromCart(); //errore 500
        }
    }

}
