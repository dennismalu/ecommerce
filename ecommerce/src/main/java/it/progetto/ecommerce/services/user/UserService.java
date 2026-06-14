package it.progetto.ecommerce.services.user;

import it.progetto.ecommerce.model.dto.SignUpDTO;
import it.progetto.ecommerce.model.entities.UserEntity;
import it.progetto.ecommerce.model.exceptions.CustomException;

public interface UserService {

    UserEntity getAdminUserEntity();
    boolean hasUserWithEmail(String email);
    UserEntity createUser(SignUpDTO signUpDTO) throws CustomException;

}
