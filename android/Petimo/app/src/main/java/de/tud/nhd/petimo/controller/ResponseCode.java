package de.tud.nhd.petimo.controller;

/**
 * Created by nhd on 02.09.17.
 */
// TODO make use of exceptions for error responses
public enum ResponseCode {
    INVALID_INPUT_STRING_DATE,
    INVALID_INPUT_STRING_TIME,
    INVALID_INPUT_STRING_NAME,
    INVALID_TIME,
    INVALID_TASK,
    INVALID_CATEGORY,
    DB_ERROR,
    OK;
}
