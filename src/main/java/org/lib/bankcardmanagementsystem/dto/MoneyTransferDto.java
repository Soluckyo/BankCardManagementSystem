package org.lib.bankcardmanagementsystem.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MoneyTransferDto {
    private Long cardIdFrom;
    private Long cardIdTo;
    private BigDecimal amount;
}
