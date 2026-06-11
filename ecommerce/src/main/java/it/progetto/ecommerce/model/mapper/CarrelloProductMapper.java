package it.progetto.ecommerce.model.mapper;

import it.progetto.ecommerce.model.dto.CarrelloProductDTO;
import it.progetto.ecommerce.model.dto.CategoryDTO;
import it.progetto.ecommerce.model.entities.CarrelloProductEntity;
import it.progetto.ecommerce.model.entities.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

//deve usare ProductMapper per mappare da ProductEntity a ProductDTO e viceversa
@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface CarrelloProductMapper {
    // Conversione da Entity a DTO
    @Mapping(target = "product", source = "product")
    CarrelloProductDTO toDto(CarrelloProductEntity carrelloProductEntity);

    // Conversione da DTO a Entity
    @Mapping(target = "product", source = "product")
    CarrelloProductEntity toEntity(CarrelloProductDTO dto);
}
