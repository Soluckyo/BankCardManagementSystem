package org.lib.bankcardmanagementsystem.service;

import org.lib.bankcardmanagementsystem.dto.CardCreateDTO;
import org.lib.bankcardmanagementsystem.entity.Card;
import org.lib.bankcardmanagementsystem.exception.CardNotFoundException;
import org.lib.bankcardmanagementsystem.exception.UserNotFoundException;
import org.lib.bankcardmanagementsystem.repository.CardRepository;
import org.lib.bankcardmanagementsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class CardService implements ICardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Value("${card.bin}")
    private String BIN;

    public CardService(CardRepository cardRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
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
        Card card = Card.builder()
                .encryptedCardNumber()
                .maskedCardNumber()
                //TODO: убрать метод в userService
                .ownerUser(userRepository.findById(createDTO.getOwnerId())
                        .orElseThrow(() -> new UserNotFoundException("Такой пользователь не найден!")))
                .expiryDate()
                .status()
                .balance()
                .build();

        return cardRepository.save(card);
    }

    public String encryptCardNumber(String cardNumber) {

    }

    public String generateCardNumber() {
        StringBuilder cardNumber = new StringBuilder(BIN);
        for(int i = 0; i < 9; i++) {
            cardNumber.append((int) (Math.random() * 10));
        }
        int checkDigit = getLuhnCheckDigit(cardNumber.toString());
        cardNumber.append(checkDigit);
        return cardNumber.toString();
    }

    private int getLuhnCheckDigit(String number) {
        int sum = 0;
        for(int i = 0; i < number.length(); i++) {
            int digit = Character.getNumericValue(number.charAt(number.length() - 1 - i));
            if(digit % 2 == 0){
                digit = digit * 2;
                if(digit > 9){
                    digit = digit - 9;
                }
            }
            sum = sum + digit;
        }
        return (10 - (sum % 10)) % 10;
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
