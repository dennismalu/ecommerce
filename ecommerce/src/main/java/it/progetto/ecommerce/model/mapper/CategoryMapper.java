package it.progetto.ecommerce.model.mapper;

import it.progetto.ecommerce.model.dto.CategoryDTO;
import it.progetto.ecommerce.model.entities.CategoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // Conversione da Entity a DTO
    CategoryDTO toDto(CategoryEntity categoryEntity);

    // Conversione da DTO a Entity
    CategoryEntity toCategory(CategoryDTO dto);
}
