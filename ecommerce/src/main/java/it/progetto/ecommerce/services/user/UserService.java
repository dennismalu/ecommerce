package it.progetto.ecommerce.services.user;

import it.progetto.ecommerce.model.dto.authentication.SignUpDTO;
import it.progetto.ecommerce.model.dto.authentication.UserDTO;
import it.progetto.ecommerce.model.entities.UserEntity;

public interface UserService {

    UserEntity createUser(SignUpDTO signUpDTO);
    boolean hasUserWithEmail(String email);

}
