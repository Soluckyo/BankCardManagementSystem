package org.lib.bankcardmanagementsystem.service;

import jakarta.transaction.Transactional;
import org.lib.bankcardmanagementsystem.dto.CardCreateDTO;
import org.lib.bankcardmanagementsystem.entity.Card;
import org.lib.bankcardmanagementsystem.entity.Status;
import org.lib.bankcardmanagementsystem.exception.CardNotFoundException;
import org.lib.bankcardmanagementsystem.repository.CardRepository;
import org.lib.bankcardmanagementsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class CardService implements ICardService {

    @Value("${card.expiry.year}")
    private Long EXPIRY_YEARS;

    @Value("${card.start.balance}")
    private BigDecimal START_BALANCE;

    private final CardRepository cardRepository;
    private final CardNumberGenerator cardNumberGenerator;
    private final UserService userService;

    public CardService(CardRepository cardRepository, CardNumberGenerator cardNumberGenerator, UserService userService) {
        this.cardRepository = cardRepository;
        this.cardNumberGenerator = cardNumberGenerator;
        this.userService = userService;
    }

    public Page<Card> getAllCards(Pageable pageable) {
        if(cardRepository.findAll(pageable).isEmpty()) {
            throw new CardNotFoundException("Карты не найдены");
        }
        return cardRepository.findAll(pageable);
    }

    //метод создает новую карту с изначальным балансом в 10.00 и сроком годности 4 года
    @Transactional
    public Card createCard(CardCreateDTO createDTO) {
        if(createDTO == null) {
            return null;
        }

        String numberCard = cardNumberGenerator.generateCardNumber();
        String encryptNumberCard = cardNumberGenerator.encryptCardNumber(numberCard);
        String maskedNumberCard = cardNumberGenerator.maskCardNumber(numberCard);

        Card card = Card.builder()
                .encryptedCardNumber(encryptNumberCard)
                .maskedCardNumber(maskedNumberCard)
                .ownerUser(userService.getUserById(createDTO.getOwnerId()))
                .expiryDate(LocalDate.now().plusYears(EXPIRY_YEARS))
                .status(Status.ACTIVE)
                .balance(START_BALANCE)
                .build();

        return cardRepository.save(card);
    }

    public String blockedCard(Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new CardNotFoundException("Карта не найдена")
        );
        card.setStatus(Status.BLOCKED);
        cardRepository.save(card);
        return "Карта успешно заблокирована";
    }

    public String activateCard(Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new CardNotFoundException("Карта не найдена")
        );
        card.setStatus(Status.ACTIVE);
        cardRepository.save(card);
        return "Карта успешно разблокирована";
    }

    public BigDecimal getBalance(Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new CardNotFoundException("Карта не найдена")
        );
        return card.getBalance();
    }

    @Transactional
    public BigDecimal moneyTransferCard(Long cardIdSender, Long cardIdRecipient, BigDecimal amount) {
        return null;
    }
}
