package it.progetto.ecommerce.services.user;


import it.progetto.ecommerce.model.entities.UserDetailsEntity;
import it.progetto.ecommerce.model.entities.UserEntity;
import it.progetto.ecommerce.model.enums.UserRole;
import it.progetto.ecommerce.model.mapper.UserMapper;
import it.progetto.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    //SERVE SOLO nell'Authentication Controller in fase di login/autenticazione

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findFirstByEmail(email);
        if(userEntity == null) {
            throw new UsernameNotFoundException(email + " not found", null);
        }

        UserDetailsEntity userDetailsEntity = userMapper.toUserDetailsEntity(userEntity);

        return new org.springframework.security.core.userdetails.User(
                userDetailsEntity.getEmail(),  //username
                userDetailsEntity.getPassword(),  //password
                userDetailsEntity.getAuthorities()  //authorities
        );
    }
}

