package it.progetto.ecommerce.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecutiryConfiguration {
    private final JwtRequestFilter authFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors() //senza .cors() Spring Security intercetta la richiesta OPTIONS prima
                        //che arrivi alla configurazione CORS, e risponde senza gli header CORS
                .and()
                .csrf()
                .disable() //disattivo cors e csrf
                .authorizeHttpRequests()
                .requestMatchers("/authenticate", "/sign-up").permitAll() //endpoint che non vanno protetti con token JWT
                .and()
                .authorizeHttpRequests().requestMatchers("/api/**").authenticated() //endpoint che vanno protetti con token JWT
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    //uso un'unica istanza di BCrypt() - come spiegato durante il corso il garbage collector di un'applicazione web non è performante
    //come quello di una semplice applicazione Java, passa molto meno spesso --> fare tante new porta a saturare la memoria!


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
