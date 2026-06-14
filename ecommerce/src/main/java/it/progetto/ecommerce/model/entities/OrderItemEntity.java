package it.progetto.ecommerce.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    @ManyToOne(fetch=FetchType.LAZY, optional = false)
    @JoinColumn(name="order_id", nullable = false) //, referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    //se viene eliminato un utente NON vengono eliminati anche tutti i suoi ordini
    @JsonIgnore
    private OrderEntity order;

    @ManyToOne(fetch=FetchType.LAZY, optional = false)
    @JoinColumn(name="product_id", nullable = false) //, referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    //se viene eliminato un utente NON vengono eliminati anche tutti i suoi ordini
    private ProductEntity product;

}

