package it.progetto.ecommerce.model.dto;

import it.progetto.ecommerce.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationResponseDTO {
    private String jwtToken;
    private String name;
    //private String email;
    //private Long userId;
    private UserRole role;
    //private byte[] profileImg;
}

