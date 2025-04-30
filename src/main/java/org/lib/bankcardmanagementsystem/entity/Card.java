package org.lib.bankcardmanagementsystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCard;

    private String encryptedCardNumber;

    private String maskedCurdNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    private User ownerUser;

    private LocalDate expiryDate;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    private BigDecimal balance;
}
