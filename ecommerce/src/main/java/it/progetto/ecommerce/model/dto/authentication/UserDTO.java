package it.progetto.ecommerce.model.dto.authentication;

import it.progetto.ecommerce.model.enums.UserRole;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private UserRole role;
}
