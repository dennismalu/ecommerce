package it.progetto.ecommerce.services.product;

import it.progetto.ecommerce.model.dto.ProductDTO;
import it.progetto.ecommerce.model.dto.pagedResponses.PageEntityResponseDTO;
import it.progetto.ecommerce.model.entities.ProductEntity;
import it.progetto.ecommerce.model.exceptions.CustomException;

import java.util.List;

public interface ProductService {
    List<ProductDTO> getAllProductsDTO();
    PageEntityResponseDTO<ProductDTO> getAllProductsDTOFilteredSortered(boolean disabled, int page, int size, int sort, long category);
    boolean hasProductWithName(String name);
    PageEntityResponseDTO<ProductDTO> searchProductsByNameFilteredSortered(boolean withDisabled, int page, int size, int sort, long category, String search);
    ProductEntity createProduct(ProductDTO productDTO) throws CustomException;
    ProductDTO getProduct(Long id) throws CustomException;
    ProductEntity updateProduct(ProductDTO productDTO) throws CustomException;
    void disableProduct(Long id) throws CustomException;
    void enableProduct(Long id) throws CustomException;
    void deleteProduct(Long id) throws CustomException;
}
