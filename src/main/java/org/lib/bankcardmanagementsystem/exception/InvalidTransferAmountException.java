package org.lib.bankcardmanagementsystem.exception;


public class InvalidTransferAmountException extends RuntimeException {
    public InvalidTransferAmountException(String message) {
        super(message);
    }
}
