package org.lib.bankcardmanagementsystem.service;

import org.lib.bankcardmanagementsystem.dto.CardDto;
import org.lib.bankcardmanagementsystem.dto.MoneyTransferDto;
import org.lib.bankcardmanagementsystem.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ICardService {
    Page<CardDto> getAllCards(Pageable pageable);
    Page<CardDto> getAllCardsByOwnerId(String authHeader, Pageable pageable);
    CardDto createCard(Long userId);
    CardDto blockedCard(Long cardId);
    CardDto activateCard(Long cardId);
    BigDecimal getBalance(Long cardId);
    BigDecimal transferMoney(MoneyTransferDto moneyTransferDTO);
}
