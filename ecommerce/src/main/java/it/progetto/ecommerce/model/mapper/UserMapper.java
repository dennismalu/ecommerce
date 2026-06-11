package it.progetto.ecommerce.model.mapper;

import it.progetto.ecommerce.model.dto.UserDTO;
import it.progetto.ecommerce.model.entities.UserDetailsEntity;
import it.progetto.ecommerce.model.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Conversione da Entity a DTO
    UserDTO toDto(UserEntity user);

    // Conversione da DTO a Entity
    UserEntity toUser(UserDTO dto);

    // Conversione da UserEntity a UserDetails
    UserDetailsEntity toUserDetailsEntity(UserEntity userEntity);

    // Conversione da UserDetails a UserEntity
    UserEntity toUserEntity(UserDetailsEntity userDetailsEntity);

}
