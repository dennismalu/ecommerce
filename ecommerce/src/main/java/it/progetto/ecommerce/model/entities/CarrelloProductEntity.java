package it.progetto.ecommerce.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@Table(name = "prodotti_carrello")
public class CarrelloProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    @ManyToOne(fetch=FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", nullable = false) //, referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    //se viene eliminato l'utente vengono eliminati anche tutti prodotti nel carrello
    @JsonIgnore //per non caricarlo nel json da restituire
    private UserEntity user;

    @ManyToOne(fetch=FetchType.EAGER, optional = false)
    @JoinColumn(name="product_id", nullable = false) //, referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    //se viene eliminato un prodotto vengono eliminato anche da tutti gli ordini
    private ProductEntity product;
}

