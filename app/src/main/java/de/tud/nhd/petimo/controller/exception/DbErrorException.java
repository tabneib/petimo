package de.tud.nhd.petimo.controller.exception;

/**
 * Created by nhd on 05.09.17.
 */

public class DbErrorException extends Exception {
    public DbErrorException(String message) {
        super(message);
    }
}
