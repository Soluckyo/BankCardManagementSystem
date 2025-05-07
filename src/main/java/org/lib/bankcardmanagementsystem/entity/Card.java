package org.lib.bankcardmanagementsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "card")
@AllArgsConstructor
@NoArgsConstructor
public class Card {

    /**
     * ID карты.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_card")
    private Long idCard;

    /**
     * Зашифрованный номер карты
     */
    @Column(name = "encrypted_card_number")
    private String encryptedCardNumber;

    /**
     * Маска номера карты
     */
    @Column(name = "masked_card_number")
    private String maskedCardNumber;

    /**
     * Владелец карты.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User ownerUser;

    /**
     * Срок действия карты.
     */
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    /**
     * Статус карты.
     */
    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private Status status;

    /**
     * Баланс карты
     */
    @Column(name = "balance")
    private BigDecimal balance;

    /**
     * Запрос на блокировку карты
     */
    @Column(name = "block_request")
    private Boolean blockRequest = false;
}
