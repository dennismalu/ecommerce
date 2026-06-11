package it.progetto.ecommerce.model.exceptions;

public class DifferentUserException extends RuntimeException {
    public DifferentUserException() {
        super("Errore di autenticazione, rieffettua il login");
    }
}
