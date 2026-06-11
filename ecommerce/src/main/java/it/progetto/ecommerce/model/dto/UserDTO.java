package it.progetto.ecommerce.model.dto;

import it.progetto.ecommerce.model.enums.UserRole;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private UserRole role;
    private byte[] profileImg;
    private String lastSearch;
}

