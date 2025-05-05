package org.lib.bankcardmanagementsystem.exception;


public class InvalidAccessTokenException extends RuntimeException {
    public InvalidAccessTokenException(String message) {
        super(message);
    }
}
