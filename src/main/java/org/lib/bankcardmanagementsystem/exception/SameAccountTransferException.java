package org.lib.bankcardmanagementsystem.exception;


public class SameAccountTransferException extends RuntimeException {
    public SameAccountTransferException(String message) {
        super(message);
    }
}
