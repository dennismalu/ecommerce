package it.progetto.ecommerce.configuration;

import it.progetto.ecommerce.model.entities.UserDetailsEntity;
import it.progetto.ecommerce.model.entities.UserEntity;
import it.progetto.ecommerce.model.mapper.UserMapper;
import it.progetto.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getPrincipal().toString();
        String rawPassword = authentication.getCredentials().toString();

        UserEntity userEntity = userRepository.findFirstByEmail(email);
        if (userEntity == null) {
            throw new UsernameNotFoundException("Utente non trovato con email: " + email);
        }

        if (!passwordEncoder.matches(rawPassword, userEntity.getPassword())) {
            throw new BadCredentialsException("Password errata per l'utente: " + email);
        }

        UserDetailsEntity userDetailsEntity = userMapper.toUserDetailsEntity(userEntity);

        UsernamePasswordAuthenticationToken authenticatedToken =
                new UsernamePasswordAuthenticationToken(userDetailsEntity, null, userDetailsEntity.getAuthorities());

        authenticatedToken.setDetails(authentication.getDetails()); // opzionale

        return authenticatedToken;
    }
}