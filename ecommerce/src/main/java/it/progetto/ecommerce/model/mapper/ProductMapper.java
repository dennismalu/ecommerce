package it.progetto.ecommerce.model.mapper;

import it.progetto.ecommerce.model.dto.ProductDTO;
import it.progetto.ecommerce.model.entities.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDTO toDto(ProductEntity productEntity) {
        if (productEntity == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setId(productEntity.getId());
        dto.setName(productEntity.getName());
        dto.setDescription(productEntity.getDescription());
        dto.setPrice(productEntity.getPrice());
        dto.setDisabled(productEntity.getDisabled());
        dto.setImage(productEntity.getImage());

        // Mappa l'ID e il nome della categoria
        if (productEntity.getCategory() != null) {
            dto.setCategoryId(productEntity.getCategory().getId());
            dto.setCategoryName(productEntity.getCategory().getName());
        }

        return dto;
    }



    public ProductEntity toEntity(ProductDTO dto) {
        if (dto == null) {
            return null;
        }

        ProductEntity entity = new ProductEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setDisabled(dto.getDisabled());
        entity.setImage(dto.getImage());

        // NOTA: La categoria non viene impostata qui, deve essere gestita nel service

        return entity;
    }

    // Metodo per aggiornare un'entità esistente
    public void updateEntityFromDto(ProductDTO dto, ProductEntity entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImage(dto.getImage());
        // La categoria viene gestita separatamente
    }
}