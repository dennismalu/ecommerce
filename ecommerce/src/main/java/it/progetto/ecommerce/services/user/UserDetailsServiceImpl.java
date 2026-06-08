package it.progetto.ecommerce.services.user;


import it.progetto.ecommerce.model.entities.UserEntity;
import it.progetto.ecommerce.model.enums.UserRole;
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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findFirstByEmail(email);
        if(user == null) {
            throw new UsernameNotFoundException(email + " not found", null);
        }
        //LinkedList<GrantedAuthority> grantedAuthorities = new LinkedList<>();
        //LinkedList<UserRole> grantedAuthorities = new LinkedList<>();
        //grantedAuthorities.addLast(user.getRole());
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),  //username
                user.getPassword(),  //password
                user.getAuthorities()  //authorities
        );
    }
}
