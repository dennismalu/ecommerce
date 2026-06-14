package it.progetto.ecommerce.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@Table(name = "prodotti_lista_desideri")
public class ListaDesideriProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", nullable = false) //, referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    //se viene eliminato l'utente vengono eliminati anche tutti prodotti nella lista dei desideri
    @JsonIgnore //per non caricarlo nel json da restituire
    private UserEntity user;

    @ManyToOne(fetch=FetchType.EAGER, optional = false)
    @JoinColumn(name="product_id", nullable = false) //, referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    //se viene eliminato un prodotto viene rimosso anche dalla lista desideri
    private ProductEntity product;
}
