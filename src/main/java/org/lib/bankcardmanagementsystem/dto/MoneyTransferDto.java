package org.lib.bankcardmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MoneyTransferDto {
    private Long cardIdFrom;
    private Long cardIdTo;
    private BigDecimal amount;
}
