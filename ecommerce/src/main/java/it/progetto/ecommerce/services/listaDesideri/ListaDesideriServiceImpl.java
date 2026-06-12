package it.progetto.ecommerce.services.listaDesideri;

import it.progetto.ecommerce.model.dto.ListaDesideriProductDTO;
import it.progetto.ecommerce.model.entities.*;
import it.progetto.ecommerce.model.exceptions.DifferentUserException;
import it.progetto.ecommerce.model.exceptions.ProductNotFoundException;
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
    public void addToListaDesideri(long idProdotto) throws ProductNotFoundException {
        ProductEntity productEntity = productRepository.findFirstById(idProdotto);
        if(productEntity == null) {
            throw new ProductNotFoundException();
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsEntity userDetailsEntity = (UserDetailsEntity) auth.getPrincipal();
        UserEntity userEntity = userMapper.toUserEntity(userDetailsEntity);

        ListaDesideriProductEntity listaDesideriProductEntity = new ListaDesideriProductEntity();
        listaDesideriProductEntity.setUser(userEntity);
        listaDesideriProductEntity.setProduct(productEntity);
        listaDesideriProductRepository.save(listaDesideriProductEntity);
    }

    @Override
    public void removeFromListaDesideri(long idProdottoListaDesideri) throws DifferentUserException, ProductNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsEntity userDetailsEntity = (UserDetailsEntity) auth.getPrincipal();

        ListaDesideriProductEntity listaDesideriProductEntity = listaDesideriProductRepository.findFirstById(idProdottoListaDesideri);

        if(listaDesideriProductEntity == null) {
            throw new ProductNotFoundException();
        }
        else if(!listaDesideriProductEntity.getUser().getId().equals(userDetailsEntity.getId())) {
            throw new DifferentUserException();
        }

        listaDesideriProductRepository.delete(listaDesideriProductEntity);
    }
}
