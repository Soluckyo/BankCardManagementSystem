package org.lib.bankcardmanagementsystem.service;

import org.lib.bankcardmanagementsystem.dto.CardDto;
import org.lib.bankcardmanagementsystem.dto.MoneyTransferDto;
import org.lib.bankcardmanagementsystem.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ICardService {
    Page<CardDto> getAllCards(Pageable pageable);
    Page<CardDto> getAllCardsByOwnerId(String authHeader, Pageable pageable, BigDecimal minBalance, Status status);
    CardDto createCard(Long userId);
    void deleteCardById(Long cardId);
    CardDto blockedCard(Long cardId);
    CardDto activateCard(Long cardId);
    BigDecimal getBalance(Long cardId);
    BigDecimal transferMoney(MoneyTransferDto moneyTransferDTO);
    String requestForBlockCard(String authHeader, Long cardId);
    Page<CardDto> findRequestCardBlock(Pageable pageable);
}
