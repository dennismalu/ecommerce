package it.progetto.ecommerce.services.listaDesideri;

import it.progetto.ecommerce.model.dto.ListaDesideriProductDTO;
import it.progetto.ecommerce.model.entities.*;
import it.progetto.ecommerce.model.exceptions.CustomExceptionBuilder;
import it.progetto.ecommerce.model.exceptions.CustomException;
import it.progetto.ecommerce.model.mapper.ListaDesideriProductMapper;
import it.progetto.ecommerce.model.mapper.UserMapper;
import it.progetto.ecommerce.repository.ListaDesideriProductRepository;
import it.progetto.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListaDesideriServiceImpl implements ListaDesideriService {

    private final ListaDesideriProductRepository listaDesideriProductRepository;
    private final ListaDesideriProductMapper listaDesideriProductMapper;
    private final ProductRepository productRepository;
    private final UserMapper userMapper;

    @Override
    public List<ListaDesideriProductDTO> getListaDesideri() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsEntity userDetailsEntity = (UserDetailsEntity) auth.getPrincipal();

        List<ListaDesideriProductEntity> listaDesideri = listaDesideriProductRepository.getAllByUser_IdOrderByProduct_Name(userDetailsEntity.getId());

        List<ListaDesideriProductDTO> listaDesideriDTO = new LinkedList<>();
        for(ListaDesideriProductEntity listaDesideriProduct: listaDesideri) {
            listaDesideriDTO.add(listaDesideriProductMapper.toDto(listaDesideriProduct));
        }
        return listaDesideriDTO;
    }

    @Override
    public void addToListaDesideri(long idProdotto) throws CustomException {
        ProductEntity productEntity = productRepository.findFirstById(idProdotto);
        if(productEntity == null) {
            throw CustomExceptionBuilder.productNotFound(); //errore 404
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsEntity userDetailsEntity = (UserDetailsEntity) auth.getPrincipal();
        UserEntity userEntity = userMapper.toUserEntity(userDetailsEntity);

        ListaDesideriProductEntity listaDesideriProductEntity = new ListaDesideriProductEntity();
        listaDesideriProductEntity.setUser(userEntity);
        listaDesideriProductEntity.setProduct(productEntity);
        try {
            listaDesideriProductRepository.save(listaDesideriProductEntity);
        } catch(Exception e) {
            throw CustomExceptionBuilder.productNotAddedToListaDesideri(); //errore 500
        }
    }

    @Override
    public void removeFromListaDesideri(long idProdottoListaDesideri) throws CustomException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsEntity userDetailsEntity = (UserDetailsEntity) auth.getPrincipal();

        ListaDesideriProductEntity listaDesideriProductEntity = listaDesideriProductRepository.findFirstById(idProdottoListaDesideri);

        if(listaDesideriProductEntity == null) {
            throw CustomExceptionBuilder.productNotFound(); //errore 404
        }
        else if(!listaDesideriProductEntity.getUser().getId().equals(userDetailsEntity.getId())) {
            throw CustomExceptionBuilder.userNotAllowed(); //errore 405
        }

        try {
            listaDesideriProductRepository.delete(listaDesideriProductEntity);
        } catch(Exception e) {
            throw CustomExceptionBuilder.productNotRemovedFromListaDesideri(); //errore 500
        }
    }

}


