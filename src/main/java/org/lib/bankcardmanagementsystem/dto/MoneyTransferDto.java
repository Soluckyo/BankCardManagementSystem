package org.lib.bankcardmanagementsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для переводов")
public class MoneyTransferDto {

    @Schema(description = "ID карты с которой совершается перевод")
    private Long cardIdFrom;

    @Schema(description = "ID карты на которую будет совершаться перевод")
    private Long cardIdTo;

    @Schema(description = "Сумма перевода")
    private BigDecimal amount;
}
