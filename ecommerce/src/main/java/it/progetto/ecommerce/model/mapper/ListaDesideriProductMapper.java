package it.progetto.ecommerce.model.mapper;


import it.progetto.ecommerce.model.dto.CarrelloProductDTO;
import it.progetto.ecommerce.model.dto.ListaDesideriProductDTO;
import it.progetto.ecommerce.model.entities.CarrelloProductEntity;
import it.progetto.ecommerce.model.entities.ListaDesideriProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

//deve usare ProductMapper per mappare da ProductEntity a ProductDTO e viceversa
@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface ListaDesideriProductMapper {
    // Conversione da Entity a DTO
    @Mapping(target = "product", source = "product")
    ListaDesideriProductDTO toDto(ListaDesideriProductEntity listaDesideriProductEntity);

    // Conversione da DTO a Entity
    @Mapping(target = "product", source = "product")
    ListaDesideriProductEntity toEntity(ListaDesideriProductDTO listaDesideriProductDTO);
}