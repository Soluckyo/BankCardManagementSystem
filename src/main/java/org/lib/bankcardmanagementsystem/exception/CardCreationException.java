package org.lib.bankcardmanagementsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CardCreationException extends RuntimeException {
    public CardCreationException(String message) {
        super(message);
    }
}
