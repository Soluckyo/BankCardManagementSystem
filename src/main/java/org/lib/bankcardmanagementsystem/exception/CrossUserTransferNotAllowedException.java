package org.lib.bankcardmanagementsystem.exception;


public class CrossUserTransferNotAllowedException extends RuntimeException {
    public CrossUserTransferNotAllowedException(String message) {
        super(message);
    }
}
