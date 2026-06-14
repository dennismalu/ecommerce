package it.progetto.ecommerce.model.exceptions;

public class CustomExceptionBuilder {

    public static CustomException userAlreadyRegistered() {
        return new CustomException("Utente già registrato", 409); //CONFLICT
    }

    public static CustomException userNotCreated() {
        return new CustomException("Utente non creato", 500); //SERVER_ERROR
    }

    public static CustomException categoryNotFound(){
        return new CustomException("Categoria non trovata", 404); //NOT_FOUND
    }

    public static CustomException categoryAlreadyExists(){
        return new CustomException("Categoria già esistente", 409); //CONFLICT
    }


    public static CustomException categoryNotCreated() {
        return new CustomException("Categoria non salvata", 500); //SERVER_ERROR
    }

    public static CustomException productNotFound(){
        return new CustomException("Prodotto non trovato", 404); //NOT_FOUND
    }

    public static CustomException productAlreadyExists(){
        return new CustomException("Prodotto già esistente", 409); //CONFLICT
    }

    public static CustomException productNotCreated(){
        return new CustomException("Prodotto non salvato", 500); //SERVER_ERROR
    }

    public static CustomException productNotUpdated(){
        return new CustomException("Prodotto non aggiornato", 500); //SERVER_ERROR
    }

    public static CustomException productNotEnabled(){
        return new CustomException("Prodotto non abilitato", 500); //SERVER_ERROR
    }

    public static CustomException productNotDisabled(){
        return new CustomException("Prodotto non disabilitato", 500); //SERVER_ERROR
    }

    public static CustomException productNotDeleted(){
        return new CustomException("Prodotto non eliminato", 500); //SERVER_ERROR
    }

    public static CustomException productNotAddedToCart() {
        return new CustomException("Prodotto non inserito nel carrello", 500); //SERVER_ERROR
    }

    public static CustomException cartNotUpdated() {
        return new CustomException("Carrello non aggiornato", 500); //SERVER_ERROR
    }

    public static CustomException productNotRemovedFromCart() {
        return new CustomException("Prodotto non rimosso dal carrello", 500); //SERVER_ERROR
    }

    public static CustomException productNotAddedToListaDesideri() {
        return new CustomException("Prodotto non inserito nella lista desideri", 500); //SERVER_ERROR
    }

    public static CustomException productNotRemovedFromListaDesideri() {
        return new CustomException("Prodotto non rimosso dalla lista desideri", 500); //SERVER_ERROR
    }

    public static CustomException orderFailed() {
        return new CustomException("Ordine non riuscito", 500); //SERVER_ERROR
    }

    public static CustomException prezziNonAggiornati() {
        return new CustomException("Errore, per favore ricarica la pagina", 406); //NOT_ACCEPTABLE
    }

    public static CustomException userNotAllowed() {
        return new CustomException("Utente non autorizzato", 405); //METHOD_NOT_ALLOWED
    }

}
