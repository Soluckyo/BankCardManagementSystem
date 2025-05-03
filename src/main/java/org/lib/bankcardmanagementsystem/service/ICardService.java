package org.lib.bankcardmanagementsystem.service;

import org.lib.bankcardmanagementsystem.dto.CardCreateDTO;
import org.lib.bankcardmanagementsystem.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ICardService {
    Page<Card> getAllCards(Pageable pageable);
    Card createCard(CardCreateDTO createDTO);
    String blockedCard(Long cardId);
    String activateCard(Long cardId);
    BigDecimal getBalance(Long cardId);
    BigDecimal moneyTransferCard(Long cardIdSender, Long cardIdRecipient, BigDecimal amount);
}
