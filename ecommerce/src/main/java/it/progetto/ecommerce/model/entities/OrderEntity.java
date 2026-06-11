package it.progetto.ecommerce.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.progetto.ecommerce.model.enums.OrderStatus;
import it.progetto.ecommerce.model.enums.PagamentType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private String address;

    private PagamentType pagamentType; //enum: portafoglio piattaforma, pagamento alla consegna

    private Date date;

    private Double price;

    private OrderStatus orderStatus; //INLAVORAZIONE - SPEDITO

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name="user_id", nullable = false) //, referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    //se viene eliminato un utente vengono eliminati anche tutti i suoi ordini
    private UserEntity user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItemEntity> orderItems;

}
