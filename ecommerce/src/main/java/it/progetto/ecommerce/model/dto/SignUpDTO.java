package it.progetto.ecommerce.model.dto;

import lombok.Data;

@Data
public class SignUpDTO {
    private String name;
    private String email;
    private String password;
}
