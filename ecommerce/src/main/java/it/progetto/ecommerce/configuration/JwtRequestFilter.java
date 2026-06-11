package it.progetto.ecommerce.configuration;

import it.progetto.ecommerce.model.entities.UserDetailsEntity;
import it.progetto.ecommerce.model.entities.UserEntity;
import it.progetto.ecommerce.model.mapper.UserMapper;
import it.progetto.ecommerce.repository.UserRepository;
import it.progetto.ecommerce.services.user.UserDetailsServiceImpl;
import it.progetto.ecommerce.services.jwt.JwtService;
import it.progetto.ecommerce.services.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.LinkedList;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    //OncePerRequestFilter --> il filtro viene eseguito una sola volta per ogni richiesta HTTP


    //private final UserDetailsServiceImpl userDetailsService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String email = null;

        //TOKEN PRESENTE
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            //se l'intestazione inizia con "Bearer " il token JWT è presente
            String token = authHeader.substring(7);
            try {
                email = jwtService.extractEmail(token);
            } catch (Exception e) { } //email = null

            //Token corretto
            if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                //verifica se l'utente non è già autenticato controllando il contesto di sicurezza (SecurityContextHolder)
                //--> se getAuthentication() è null significa che l'utente non è autenticato

                UserEntity userEntity = userRepository.findFirstByEmail(email);
                if(userEntity == null) {
                    throw new UsernameNotFoundException(email + " not found", null);
                }
                UserDetailsEntity userDetailsEntity = userMapper.toUserDetailsEntity(userEntity);

                //se l'utente non è autenticato verifico il suo token e, se è tutto ok, lo autentico
                //(aggiungo il token al contesto di sicurezza --> l'autenticazione sarà valida solo per la chiamata http corrente)
                if(jwtService.validateToken(token, userDetailsEntity)) { //email corretta e token non scaduto
                    //UsernamePasswordAuthenticationToken è un oggetto che rappresenta l'autenticazione dell'utente
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetailsEntity,
                                    null, //non è necessario fornire una password perché l'autenticazione è basata sul token
                                    userDetailsEntity.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); //imposta i dettagli dell'autenticazione (indirizzo IP e sessione)
                    SecurityContextHolder.getContext().setAuthentication(authToken); //imposta l'autenticazione nel contesto di sicurezza
                    //L'autenticazione viene mantenuta (solo) per tutta la durata della richiesta corrente; per le richieste successive si usa
                    //il token JWT e un filtro che lo verifichi e imposti nuovamente l'autenticazione nel contesto di sicurezza
                    //--> questo permette di fare l'autenticazione ogni volta qui senza doverla fare nei metodi successivi
                }
                else{
                    // Token scaduto o non valido
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("""
                    {"error": "%s", "message": "%s"}
                    """.formatted(
                            HttpStatus.valueOf(HttpServletResponse.SC_UNAUTHORIZED).getReasonPhrase(),
                            "Token scaduto o non valido")
                    );
                    return;
                    //Importante: non bisogna chiamare filterChain.doFilter(...) dopo aver
                    //inviato una risposta, altrimenti si potrebbe causare un'eccezione
                }
            }
            //token scorretto (stringa sbagliata)
            else{
                //email=null --> token non valido (stringa sbagliata)
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("""
                    {"error": "%s", "message": "%s"}
                    """.formatted(
                        HttpStatus.valueOf(HttpServletResponse.SC_UNAUTHORIZED).getReasonPhrase(),
                        "Token scaduto o non valido")
                );
                return;
            }
        }
        //else --> TOKEN NON PRESENTE

        filterChain.doFilter(request, response); //invoca il prossimo filtro nella catena di filtri Spring Security

    }
}
