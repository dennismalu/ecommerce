package it.progetto.ecommerce.model.exceptions;

public class CustomException extends RuntimeException {
    private int statusCode;

    public int getStatusCode() {
        return statusCode;
    }

    public CustomException(String message, int code) {
        super(message);
        this.statusCode = code;
    }
}

