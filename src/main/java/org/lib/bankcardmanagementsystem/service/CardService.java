package org.lib.bankcardmanagementsystem.service;

import org.lib.bankcardmanagementsystem.dto.CardCreateDTO;
import org.lib.bankcardmanagementsystem.entity.Card;
import org.lib.bankcardmanagementsystem.exception.CardNotFoundException;
import org.lib.bankcardmanagementsystem.repository.CardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CardService implements ICardService {

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public Page<Card> getAllCards(Pageable pageable) {
        if(cardRepository.findAll(pageable).isEmpty()) {
            throw new CardNotFoundException("Карты не найдены");
        }
        return cardRepository.findAll(pageable);
    }

    public Card createCard(CardCreateDTO createDTO) {
        if(createDTO == null) {
            return null;
        }
        return null;
    }
    
    public Card blockedCard(Card card) {
        return null;
    }

    public Card activateCard(Card card) {
        return null;
    }

    public BigDecimal getBalance(Card card) {
        return null;
    }

    public BigDecimal moneyTransferCard(Card card, BigDecimal amount) {
        return null;
    }
}
