package it.progetto.ecommerce.controllers;



import it.progetto.ecommerce.configuration.CustomAuthenticationManager;
import it.progetto.ecommerce.model.dto.AuthenticationRequestDTO;
import it.progetto.ecommerce.model.dto.AuthenticationResponseDTO;
import it.progetto.ecommerce.model.dto.SignUpDTO;
import it.progetto.ecommerce.model.entities.UserDetailsEntity;
import it.progetto.ecommerce.model.entities.UserEntity;
import it.progetto.ecommerce.model.mapper.UserMapper;
import it.progetto.ecommerce.repository.UserRepository;
import it.progetto.ecommerce.services.jwt.JwtService;
import it.progetto.ecommerce.services.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private static final String HEADER_STRING = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

 //    private final UserService userService;
    private final CustomAuthenticationManager authenticationManager; //spring
    private final UserService userService;
    private final UserMapper userMapper;
    //private final UserDetailsService userDetailsService; //spring
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequestDTO authenticationRequestDTO, HttpServletResponse response) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequestDTO.getEmail(),  //Principal (identificativo utente)
                            authenticationRequestDTO.getPassword()  //Credentials (password)
                    )
            );

            //username e password sono corretti
            UserDetailsEntity userDetails = (UserDetailsEntity) authentication.getPrincipal();
            //UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequestDTO.getEmail());
            //UserEntity user = userRepository.findFirstByEmail(userDetails.getUsername());
            String jwtToken = jwtService.generateToken(userDetails.getEmail());

            return new ResponseEntity<>(new AuthenticationResponseDTO(jwtToken, userDetails.getName(), userDetails.getRole()), HttpStatus.OK); //stato 200
            //RESTITUISCO IL TOKEN
        }
        catch(UsernameNotFoundException | BadCredentialsException e){
            return new ResponseEntity<>("Username o password non validi!", HttpStatus.UNAUTHORIZED); //stato 401 (errore di login o utente non registrato)
        }
        catch(DisabledException disabledException){
            return new ResponseEntity<>("L'utente non è attivato", HttpStatus.NOT_ACCEPTABLE); //stato 406
        }
    }




    @PostMapping("/sign-up")
    public ResponseEntity<?> signUpUser(@RequestBody SignUpDTO signUpDTO){
        if(userService.hasUserWithEmail(signUpDTO.getEmail())){
            return new ResponseEntity<>("Utente già registrato", HttpStatus.NOT_ACCEPTABLE); //stato 406
        }
        UserEntity createdUser = userService.createUser(signUpDTO);
        if(createdUser == null){
            return new ResponseEntity<>("Utente non creato!", HttpStatus.BAD_REQUEST); //stato 400
        }
        //UserDTO createdUserDto = userMapper.toDto(createdUser);
        return new ResponseEntity<>(HttpStatus.CREATED); //stato 201
    }
}
