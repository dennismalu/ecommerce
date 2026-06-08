package it.progetto.ecommerce.services.product;

import it.progetto.ecommerce.model.dto.ProductDTO;
import it.progetto.ecommerce.model.dto.pageResponse.PageEntityResponseDTO;
import it.progetto.ecommerce.model.entities.ProductEntity;

import java.util.HashMap;
import java.util.List;

public interface ProductService {
    List<ProductDTO> getAllProductsDTO();
    PageEntityResponseDTO<ProductDTO> getAllProductsDTOFilteredSortered(boolean disabled, int page, int size, int sort, long category);
    boolean hasProductWithName(String name);
    PageEntityResponseDTO<ProductDTO> searchProductsByNameFilteredSortered(boolean withDisabled, int page, int size, int sort, long category, String name);
    ProductEntity createProduct(ProductDTO productDTO);
    ProductDTO getProduct(Long id);
    ProductEntity updateProduct(ProductDTO productDTO);
    boolean disableProduct(Long id);
    boolean enableProduct(Long id);
    boolean deleteProduct(Long id);
}
