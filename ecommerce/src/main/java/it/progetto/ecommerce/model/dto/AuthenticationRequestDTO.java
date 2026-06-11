package it.progetto.ecommerce.model.dto;

import lombok.Data;

@Data
public class AuthenticationRequestDTO {

    private String email;
    private String password;

}
