package it.progetto.ecommerce.controllers;



import it.progetto.ecommerce.model.dto.authentication.AuthenticationRequestDTO;
import it.progetto.ecommerce.model.dto.authentication.AuthenticationResponseDTO;
import it.progetto.ecommerce.model.dto.authentication.SignUpDTO;
import it.progetto.ecommerce.model.dto.authentication.UserDTO;
import it.progetto.ecommerce.model.entities.UserEntity;
import it.progetto.ecommerce.model.mapper.UserMapper;
import it.progetto.ecommerce.repository.UserRepository;
import it.progetto.ecommerce.services.jwt.JwtService;
import it.progetto.ecommerce.services.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final AuthenticationManager authenticationManager; //spring
    private final UserService userService;
    private final UserMapper userMapper;
    //private final UserDetailsService userDetailsService; //spring
    private final UserRepository userRepository;
    private final JwtService jwtService;


//    @PostMapping("/authenticate")
//    public AuthenticationResponseDTO createAuthenticationToken(@RequestBody AuthenticationRequestDTO authenticationRequest, HttpServletResponse response)
//            throws IOException, JSONException {
//        try{
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                        authenticationRequest.getEmail(), authenticationRequest.getPassword()
//                )
//            );
//        } catch(BadCredentialsException e){
//            throw new BadCredentialsException("Username o password errata!");
//            //response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Utente già registrato");
//            //return null;
//        } catch(DisabledException disabledException){
//            response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "L'utente non è attivato");
//            return null;
//        }
//
//        //username e password sono corretti
//        //UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
//        //UserEntity user = userRepository.findFirstByEmail(userDetails.getUsername());
//        //String jwt = jwtUtil.generateToken(userDetails.getUsername());
//
//        String jwt = jwtUtil.generateToken(authenticationRequest.getEmail());
//        return new AuthenticationResponseDTO(jwt);
//
////        if(optionalUser.isPresent()){
////            response.getWriter().write(new JSONObject()
////                    .put("userId", optionalUser.get().getId())
////                    .put("role", optionalUser.get().getRole())
////                    .toString());
////
////            response.addHeader(HEADER_STRING, TOKEN_PREFIX + jwt);
////        }
//    }

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
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        //UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequestDTO.getEmail());
        UserEntity user = userRepository.findFirstByEmail(userDetails.getUsername());
        String jwtToken = jwtService.generateToken(authenticationRequestDTO.getEmail());


//        response.getWriter().write(new JSONObject()
//                .put("userID", user.getId())
//                .put("role", user.getRole())
//                .toString()
//        );
//        response.getWriter().flush();

        //response.addHeader("Access-Control-Expose-Headers", "Authorization");
        //response.addHeader("Access-Control-Allow-Origin", "Authorization,X-PINGGOTHER,Origin,X-Requested-With,Content-Type,Accept,X-CostumeHeader");
        //response.addHeader(HEADER_STRING, TOKEN_PREFIX + jwtToken);
        return new ResponseEntity<>(new AuthenticationResponseDTO(jwtToken, user.getName(), user.getId(), user.getRole()), HttpStatus.OK); //stato 200
        //RESTITUISCO IL TOKEN

    } catch(UsernameNotFoundException | BadCredentialsException e){
        //throw new RuntimeException(e);
        return new ResponseEntity<>("Username o password non validi!", HttpStatus.UNAUTHORIZED); //stato 401 (errore di login o utente non registrato)
    } catch(DisabledException disabledException){
        //throw new RuntimeException(disabledException);
        return new ResponseEntity<>("L'utente non è attivato", HttpStatus.NOT_ACCEPTABLE); //stato 406
//    } catch (JSONException e) {
//        throw new RuntimeException(e);
//    } catch (IOException e) {
//        throw new RuntimeException(e);
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
        UserDTO createdUserDto = userMapper.toDto(createdUser);
        return new ResponseEntity<>(createdUserDto, HttpStatus.CREATED); //stato 201
    }
}
