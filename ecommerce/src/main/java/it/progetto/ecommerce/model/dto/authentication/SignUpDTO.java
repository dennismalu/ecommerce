package it.progetto.ecommerce.model.dto.authentication;

import lombok.Data;

@Data
public class SignUpDTO {
    private String name;
    private String email;
    private String password;
}
