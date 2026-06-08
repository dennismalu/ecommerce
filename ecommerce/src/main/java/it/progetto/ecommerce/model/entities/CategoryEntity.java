package it.progetto.ecommerce.model.entities;

import it.progetto.ecommerce.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "categories")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) //indica che il valore della colonna deve essere unico
    private String name;

    @Column(columnDefinition = "TEXT")
    //forzo TEXT --> può contenere più dati di VARCHAR(255)
    private String description;

}

