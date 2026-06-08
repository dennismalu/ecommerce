package it.progetto.ecommerce.model.dto.authentication;

import lombok.Data;

@Data
public class AuthenticationRequestDTO {

    private String email;
    private String password;

}
