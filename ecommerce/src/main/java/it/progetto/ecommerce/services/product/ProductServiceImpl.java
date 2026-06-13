package it.progetto.ecommerce.services.product;

import it.progetto.ecommerce.model.dto.ProductDTO;
import it.progetto.ecommerce.model.dto.pagedResponses.PageEntityResponseDTO;
import it.progetto.ecommerce.model.entities.CategoryEntity;
import it.progetto.ecommerce.model.entities.ProductEntity;
import it.progetto.ecommerce.model.entities.UserDetailsEntity;
import it.progetto.ecommerce.model.enums.UserRole;
import it.progetto.ecommerce.model.exceptions.CustomExceptionBuilder;
import it.progetto.ecommerce.model.exceptions.CustomException;
import it.progetto.ecommerce.model.mapper.ProductMapper;
import it.progetto.ecommerce.repository.CategoryRepository;
import it.progetto.ecommerce.repository.ProductRepository;
import it.progetto.ecommerce.repository.UserRepository;
import it.progetto.ecommerce.utils.ProductUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public List<ProductDTO> getAllProductsDTO() {
        List<ProductEntity> prodotti = productRepository.findAll();
        List<ProductDTO> ret = new LinkedList<>();
        for(ProductEntity product : prodotti){
            ret.add(productMapper.toDto(product));
        }
        return ret;
    }


    //per fare la GET sui tutti i prodotti (disattivati e non disattivati)
    @Override
    public PageEntityResponseDTO<ProductDTO> getAllProductsDTOFilteredSortered(boolean disabled, int page, int size, int sort, long category) {
        Sort sortConfig = ProductUtils.setSortConfig(sort);

        Pageable pageable = PageRequest.of(page, size, sortConfig);
        Page<ProductEntity> prodottiPage;
        if (category == -1) {
            // tutte le categorie (all)
            //prodottiPage = productRepository.findAll(pageable);
            prodottiPage = productRepository.findByDisabled(disabled, pageable);
        }
        else {
            prodottiPage = productRepository.findByCategory_IdAndDisabled(category, disabled, pageable);
        }
        List<ProductEntity> prodotti = prodottiPage.getContent();

        List<ProductDTO> prodottiDTO = new LinkedList<>();
        for(ProductEntity product : prodotti){
            prodottiDTO.add(productMapper.toDto(product));
        }

        return new PageEntityResponseDTO<>(
                prodottiDTO, prodottiPage.getTotalPages(), prodottiPage.getTotalElements()
        );
    }

    @Override
    public boolean hasProductWithName(String name) {
        return productRepository.findByName(name) != null;
    }

    //prodotti abilitati o tutti (abilitati e disabilitati insieme)
    @Override
    public PageEntityResponseDTO<ProductDTO> searchProductsByNameFilteredSortered(boolean withDisabled, int page, int size, int sort, long category, String search) {
        //se la richiesta è stata fatta da un utente loggato aggiorno la sua ultima ricerca
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth!=null && auth.getPrincipal() instanceof UserDetailsEntity userDetailsEntity) { //copre auth!=null
            userDetailsEntity = (UserDetailsEntity) auth.getPrincipal();
            if(userDetailsEntity!=null && userDetailsEntity.getRole().equals(UserRole.USER)){
                userRepository.updateLastSearchById(userDetailsEntity.getId(), search);
            }
        }

        Sort sortConfig = ProductUtils.setSortConfig(sort);
        Pageable pageable = PageRequest.of(page, size, sortConfig);
        Page<ProductEntity> prodottiPage;
        if (category == -1) {
            // tutte le categorie (all)
            if(withDisabled){ //se withDisabled == true --> tutti i prodotti (abilitati e disabilitati)
                prodottiPage = productRepository.findAllByNameContainingIgnoreCase(search, pageable);
            }
            else{ //se withDisabled == false --> solo prodotti non disabilitati
                prodottiPage = productRepository.findAllByNameContainingIgnoreCaseAndDisabled(search, false, pageable);
            }
        }
        else {
            if(withDisabled){ //se withDisabled == true --> tutti i prodotti (abilitati e disabilitati)
                prodottiPage = productRepository.findAllByNameContainingIgnoreCaseAndCategory_Id(search, category, pageable);
            }
            else{ //se withDisabled == false --> solo prodotti non disabilitati
                prodottiPage = productRepository.findAllByNameContainingIgnoreCaseAndCategory_IdAndDisabled(search, category, false, pageable);
            }
        }
        List<ProductEntity> prodotti = prodottiPage.getContent();

        List<ProductDTO> prodottiDTO = new LinkedList<>();
        for(ProductEntity product : prodotti){
            prodottiDTO.add(productMapper.toDto(product));
        }

        return new PageEntityResponseDTO<>(
                prodottiDTO, prodottiPage.getTotalPages(), prodottiPage.getTotalElements()
        );
    }

    @Override
    public ProductEntity createProduct(ProductDTO productDTO) throws CustomException {
        if(hasProductWithName(productDTO.getName())){
            throw CustomExceptionBuilder.productAlreadyExists(); //errore 409
        }

        CategoryEntity category = categoryRepository.findFirstById(productDTO.getCategoryId());
        if(category == null){
            throw CustomExceptionBuilder.categoryNotFound(); //errore 404
        }
        ProductEntity product = new ProductEntity();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setDisabled(false); //un prodotto appena creato non può avere disabled=true
        product.setImage(productDTO.getImage());
        product.setCategory(category);
        try {
            return productRepository.save(product);
        } catch (Exception e){
            throw CustomExceptionBuilder.productNotCreated(); //errore 500
        }
    }

    @Override
    public ProductDTO getProduct(Long id) throws CustomException {
        ProductEntity product = productRepository.findFirstById(id);
        if(product == null){
            throw CustomExceptionBuilder.productNotFound(); //errore 404
        }
        return productMapper.toDto(product);
    }

    @Override
    public ProductEntity updateProduct(ProductDTO productDTO) throws CustomException {
        ProductEntity product = productRepository.findFirstById(productDTO.getId());
        CategoryEntity category = categoryRepository.findFirstById(productDTO.getCategoryId());
        if(product == null) {
            throw CustomExceptionBuilder.productNotFound();
        }
        else if(category == null){
            throw CustomExceptionBuilder.categoryNotFound();
        }
        product.setCategory(category);
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        if(productDTO.getImage()!=null) {
            if (!productDTO.getImage().toString().isEmpty()) {
                //se l'immagine è uguale a "" lascio quella già presente nel DB
                product.setImage(productDTO.getImage());
            }
        }
        else{
            product.setImage(null);
        }
        try {
            return productRepository.save(product);
        } catch (Exception e){
            throw CustomExceptionBuilder.productNotUpdated(); //errore 500
        }
    }

    @Override
    public void disableProduct(Long id) throws CustomException {
        ProductEntity product = productRepository.findFirstById(id);
        if(product == null){
            throw CustomExceptionBuilder.productNotFound();
        }
        product.setDisabled(true);
        try {
            productRepository.save(product);
        } catch(Exception e) {
            throw CustomExceptionBuilder.productNotDisabled(); //errore 500
        }
    }

    @Override
    public void enableProduct(Long id) throws CustomException {
        ProductEntity product = productRepository.findFirstById(id);
        if(product == null){
            throw CustomExceptionBuilder.productNotFound();
        }
        product.setDisabled(false);
        try {
            productRepository.save(product);
        } catch(Exception e) {
            throw CustomExceptionBuilder.productNotEnabled(); //errore 500
        }
    }

    @Override
    public void deleteProduct(Long id) throws CustomException {
        ProductEntity product = productRepository.findFirstById(id);
        if(product == null){
            throw CustomExceptionBuilder.productNotFound();
        }
        try{
            productRepository.delete(product);
        } catch(Exception e) {
            throw CustomExceptionBuilder.productNotDeleted(); //errore 500
        }
    }

}

