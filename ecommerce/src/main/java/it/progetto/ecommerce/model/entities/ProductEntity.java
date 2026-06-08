package it.progetto.ecommerce.model.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) //indica che il valore della colonna deve essere unico
    private String name;

    @Column(columnDefinition = "TEXT")
    //forzo TEXT --> può contenere più dati di VARCHAR(255)
    private String description;

    private Double price;

    private Boolean disabled;

    private byte[] image;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) //molti prodotti --> una categoria
    //fetch = FetchType.LAZY --> i dati della categoria vengono caricati solo quando espressamente richiesti
    //optional = false --> la relazione è obbligatoria, ogni entità DEVE avere una categoria associata
    @JoinColumn(name = "categoryId", nullable = false) //non può essere NULL
    @OnDelete(action = OnDeleteAction.CASCADE)
    //se viene eliminata una categoria vengono eliminati anche tutti i prodotti associati
    @JsonIgnore
    //ignora questo campo durante la serializzazione JSON: evita ricorsioni infinite
    //quando si converte l'oggetto in JSON
    private CategoryEntity category;
}
