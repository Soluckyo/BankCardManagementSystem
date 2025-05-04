package org.lib.bankcardmanagementsystem.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class CardDto {
    private String maskedCardNumber;
    private String status;
    private String expiryDate;
    private Long ownerId;
}
