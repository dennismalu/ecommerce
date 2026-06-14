package it.progetto.ecommerce.model.dto;

import it.progetto.ecommerce.model.entities.OrderItemEntity;
import it.progetto.ecommerce.model.entities.UserEntity;
import it.progetto.ecommerce.model.enums.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;

    private String description;

    private String address;

    private Date date;

    private Double price;

    private OrderStatus orderStatus; //INLAVORAZIONE - SPEDITO

    private UserEntity user;

    private List<OrderItemEntity> orderItems;
}
