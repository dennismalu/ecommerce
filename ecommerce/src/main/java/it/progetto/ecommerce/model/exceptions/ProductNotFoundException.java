package it.progetto.ecommerce.model.exceptions;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException() {
        super("Prodotto non trovato");
    }
}
