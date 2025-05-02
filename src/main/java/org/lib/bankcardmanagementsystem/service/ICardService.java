package org.lib.bankcardmanagementsystem.service;

import org.lib.bankcardmanagementsystem.dto.CardCreateDTO;
import org.lib.bankcardmanagementsystem.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ICardService {
    Page<Card> getAllCards(Pageable pageable);
    Card createCard(CardCreateDTO createDTO);
    Card blockedCard(Card card);
    Card activateCard(Card card);
    BigDecimal getBalance(Card card);
    BigDecimal moneyTransferCard(Card card, BigDecimal amount);
    String generateCardNumber();
}
