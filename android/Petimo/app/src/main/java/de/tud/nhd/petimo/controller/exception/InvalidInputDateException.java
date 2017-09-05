package de.tud.nhd.petimo.controller.exception;

/**
 * Created by nhd on 05.09.17.
 */

public class InvalidInputDateException extends RuntimeException {

    public InvalidInputDateException(String message) {
        super(message);
    }
}
