package org.lib.bankcardmanagementsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class CrossUserTransferNotAllowedException extends RuntimeException {
    public CrossUserTransferNotAllowedException(String message) {
        super(message);
    }
}
