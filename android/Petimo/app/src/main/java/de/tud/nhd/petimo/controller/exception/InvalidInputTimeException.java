package de.tud.nhd.petimo.controller.exception;

/**
 * Created by nhd on 05.09.17.
 */

public class InvalidInputTimeException extends RuntimeException{
    public InvalidInputTimeException(String message) {
        super(message);
    }
}
