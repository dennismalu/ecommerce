package it.progetto.ecommerce.model.entities;


import it.progetto.ecommerce.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    //Mappa il campo enum UserRole su una colonna database; invece di salvare
    //l'indice numerico dell'enum salva il nome della costante enum come stringa
    //(default = EnumType.ORDINAL) --> salva il valore numerico dell'enum
    private UserRole role;

    //@Lob
    //@Column(columnDefinition = "bytea")
    private byte[] profileImg;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CarrelloProductEntity> carrelloProducts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ListaDesideriProductEntity> listaDesideriProducts;

    private String lastSearch;

}
