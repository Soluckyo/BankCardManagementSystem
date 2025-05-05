package org.lib.bankcardmanagementsystem.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CardDto {
    private Long cardId;
    private String maskedCardNumber;
    private String status;
    private String expiryDate;
    private BigDecimal balance;
    private Long ownerId;
}
