package org.lib.bankcardmanagementsystem.exception;

public class BalanceIsNotEnoughException extends RuntimeException {
    public BalanceIsNotEnoughException(String message) {
        super(message);
    }
}
