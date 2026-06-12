package it.progetto.ecommerce.services.product;

import it.progetto.ecommerce.model.dto.ProductDTO;
import it.progetto.ecommerce.model.dto.pagedResponses.PageEntityResponseDTO;
import it.progetto.ecommerce.model.entities.CategoryEntity;
import it.progetto.ecommerce.model.entities.ProductEntity;
import it.progetto.ecommerce.model.entities.UserDetailsEntity;
import it.progetto.ecommerce.model.entities.UserEntity;
import it.progetto.ecommerce.model.enums.UserRole;
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
    public ProductEntity createProduct(ProductDTO productDTO) {
        CategoryEntity category = categoryRepository.findFirstById(productDTO.getCategoryId());
        if(category != null) {
            ProductEntity product = new ProductEntity();
            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            product.setDisabled(false); //un prodotto appena creato non può avere disabled=true
            product.setImage(productDTO.getImage());
            product.setCategory(category);
            return productRepository.save(product);
        }
        return null;
    }

    @Override
    public ProductDTO getProduct(Long id) {
        ProductEntity product = productRepository.findFirstById(id);
        if(product != null) {
            return productMapper.toDto(product);
        }
        return null;
    }

    @Override
    public ProductEntity updateProduct(ProductDTO productDTO) {
        //System.out.println(productDTO.toString()); //debug
        ProductEntity product = productRepository.findFirstById(productDTO.getId());
        CategoryEntity category = categoryRepository.findFirstById(productDTO.getCategoryId());
        if(product == null || category == null) {
            return null;
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
        return productRepository.save(product);
    }

    @Override
    public boolean disableProduct(Long id) {
        ProductEntity product = productRepository.findFirstById(id);
        if(product != null) {
            product.setDisabled(true);
            productRepository.save(product);
            return true;
        }
        return false;
    }

    @Override
    public boolean enableProduct(Long id) {
        ProductEntity product = productRepository.findFirstById(id);
        if(product != null) {
            product.setDisabled(false);
            productRepository.save(product);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteProduct(Long id) {
        ProductEntity product = productRepository.findFirstById(id);
        if(product != null) {
            productRepository.delete(product);
            return true;
        }
        return false;
    }
}
