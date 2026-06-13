package it.progetto.ecommerce.services.user;

import it.progetto.ecommerce.model.dto.SignUpDTO;
import it.progetto.ecommerce.model.entities.UserEntity;
import it.progetto.ecommerce.model.enums.UserRole;
import it.progetto.ecommerce.model.exceptions.CustomExceptionBuilder;
import it.progetto.ecommerce.model.exceptions.CustomException;
import it.progetto.ecommerce.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.name}")
    private String adminName;


    //non ho bisogno di fare override, questo metodo non verrà richiamato da nessun
    //punto dell'applicazione; viene eseguito una sola volta grazie a @PostConstruct
    @PostConstruct
    public void createAdminAccount(){
        UserEntity adminAccount = userRepository.findByRole(UserRole.ADMIN);
        if(adminAccount == null){
            //non esiste un account amministratore
            UserEntity user = new UserEntity();
            user.setEmail(adminEmail);
            user.setName(adminName);
            user.setRole(UserRole.ADMIN);
            user.setPassword(bCryptPasswordEncoder.encode(adminPassword));
            userRepository.save(user); //salva nel DB
        }
    }


    @Override
    public boolean hasUserWithEmail(String email) {
        return userRepository.findFirstByEmail(email) != null;
    }


    @Override
    public UserEntity createUser(SignUpDTO signUpDTO) throws CustomException {
        if(hasUserWithEmail(signUpDTO.getEmail())){
            throw CustomExceptionBuilder.userAlreadyRegistered(); //stato 409
        }
        UserEntity user = new UserEntity();
        user.setName(signUpDTO.getName());
        user.setEmail(signUpDTO.getEmail());
        user.setRole(UserRole.USER);
        user.setPassword(bCryptPasswordEncoder.encode(signUpDTO.getPassword()));
        user.setCarrelloProducts(new LinkedList<>());
        user.setListaDesideriProducts(new LinkedList<>());
        user.setLastSearch(null);
        try {
            return userRepository.save(user); //salva nel DB
        } catch (Exception e) {
            throw CustomExceptionBuilder.userNotCreated(); //stato 500
        }
    }


}
