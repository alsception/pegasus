package org.alsception.pegasus.exception;


public class ProductValidationException extends RuntimeException {
    public ProductValidationException(String message) {
        super(message);
    }
}