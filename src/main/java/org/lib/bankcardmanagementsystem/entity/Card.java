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

/**
 * Представляет банковскую карту пользователя.
 * Связана с пользователем через поле {@link #ownerUser}.
 * Номер карты уникален и генерируется по алгоритму Луна. Не хранится в незашифрованном виде!
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Card {

    /**
     * ID карты.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCard;

    /**
     * Зашифрованный номер карты
     */
    private String encryptedCardNumber;

    /**
     * Маска номера карты
     */
    private String maskedCardNumber;

    /**
     * Владелец карты.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User ownerUser;

    /**
     * Срок действия карты.
     */
    private LocalDate expiryDate;

    /**
     * Статус карты.
     */
    @Enumerated(value = EnumType.STRING)
    private Status status;

    /**
     * Баланс карты
     */
    private BigDecimal balance;
}
