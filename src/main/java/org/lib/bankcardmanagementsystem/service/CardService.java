package org.lib.bankcardmanagementsystem.service;

import jakarta.transaction.Transactional;
import org.lib.bankcardmanagementsystem.dto.CardDto;
import org.lib.bankcardmanagementsystem.dto.MoneyTransferDto;
import org.lib.bankcardmanagementsystem.entity.Card;
import org.lib.bankcardmanagementsystem.entity.Status;
import org.lib.bankcardmanagementsystem.entity.User;
import org.lib.bankcardmanagementsystem.exception.BalanceIsNotEnoughException;
import org.lib.bankcardmanagementsystem.exception.CardCreationException;
import org.lib.bankcardmanagementsystem.exception.CardNotFoundException;
import org.lib.bankcardmanagementsystem.exception.CrossUserTransferNotAllowedException;
import org.lib.bankcardmanagementsystem.exception.InvalidTransferAmountException;
import org.lib.bankcardmanagementsystem.exception.SameAccountTransferException;
import org.lib.bankcardmanagementsystem.exception.UserNotFoundException;
import org.lib.bankcardmanagementsystem.repository.CardRepository;
import org.lib.bankcardmanagementsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Сервис для работы с картами
 */
@Service
public class CardService implements ICardService {

    private final UserRepository userRepository;
    /**
     *  Срок действия карты в годах
     *  Значение загружается из application.properties по ключу `card.expiry.year`.
     */
    @Value("${card.expiry.year}")
    private Long EXPIRY_YEAR;

    /**
     *  Стартовый баланс карты при создании
     *  Значение загружается из application.properties по ключу `card.start.balance`.
     */
    @Value("${card.start.balance}")
    private BigDecimal START_BALANCE;

    private final CardRepository cardRepository;
    private final CardNumberGenerator cardNumberGenerator;
    private final UserService userService;

    public CardService(CardRepository cardRepository, CardNumberGenerator cardNumberGenerator, UserService userService, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.cardNumberGenerator = cardNumberGenerator;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * Возвращает все карты постранично
     *
     * @param pageable параметры страницы
     * @return страница с картами
     * @throws CardNotFoundException выбрасывается, если карты не найдены
     */
    public Page<Card> getAllCards(Pageable pageable) {
        Page<Card> cards = cardRepository.findAll(pageable);
        if(cards.isEmpty()) {
            throw new CardNotFoundException("Карты не найдены");
        }
        return cards;
    }

    /**
     * Возвращает все карты, принадлежащие одному пользователю
     *
     * @param userId ID пользователя, чьи карты будут возвращены
     * @param pageable параметры страницы
     * @throws CardNotFoundException выбрасывается, если карты не найдены
     * @return страница с картами
     */
    public Page<Card> getAllCardsByOwnerId(Long userId,Pageable pageable) {
        if(userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        Page<Card> cards = cardRepository.findAllByOwnerUser_IdUser(userId ,pageable);
        if(cards.isEmpty()) {
            throw new CardNotFoundException("Карты не найдены");
        }
        return cards;
    }

    /**
     * Возвращает карту с данным ID
     *
     * @param cardId ID карты
     * @throws CardNotFoundException выбрасывается, если карты не найдены
     * @return карта с заданным ID
     */
    public Card findCardById(Long cardId) {
        return cardRepository.findById(cardId).orElseThrow(
                () -> new CardNotFoundException("Карта не найдена")
        );
    }

    /**
     * Создает новую карту с стартовым балансом START_BALANCE и сроком годности EXPIRY_YEARS
     *
     * @param userId ID пользователя
     * @return CardDto DTO, содержащий в себе строку maskedCardNumber маску карты,
     *         строку status статус карты,
     *         строку expiryDate срок действия карты
     *         числовое значение ownerId ID держателя карты
     */
    @Transactional
    public CardDto createCard(Long userId) {
        try {
            User user = userService.getUserById(userId);

            String cardNumber = cardNumberGenerator.generateCardNumber();
            String encryptNumberCard = cardNumberGenerator.encryptedNumberCard(cardNumber);
            String maskedNumberCard = cardNumberGenerator.maskedNumberCard(cardNumber);

            Card card = Card.builder()
                    .encryptedCardNumber(encryptNumberCard)
                    .maskedCardNumber(maskedNumberCard)
                    .ownerUser(user)
                    .expiryDate(LocalDate.now().plusYears(EXPIRY_YEAR))
                    .status(Status.ACTIVE)
                    .balance(START_BALANCE)
                    .build();

            cardRepository.save(card);

            return CardDto.builder()
                    .maskedCardNumber(card.getMaskedCardNumber())
                    .expiryDate(card.getExpiryDate().toString())
                    .ownerId(card.getOwnerUser().getIdUser())
                    .status(card.getStatus().toString())
                    .build();
        }catch (Exception e) {
            throw new CardCreationException("Ошибка при создании карты");
        }
    }

    /**
     * Блокирует активную карту
     *
     * @param cardId Id карты, которую необходимо разблокировать
     * @return CardDto DTO, содержащий в себе строку maskedCardNumber маску карты,
     *         строку status статус карты,
     *         строку expiryDate срок действия карты
     *         числовое значение ownerId ID держателя карты
     *
     * @throws IllegalArgumentException выбрасывается в случае, когда карта числится заблокированной
     */
    public CardDto blockedCard(Long cardId) {
        Card card = findCardById(cardId);
        if(card.getStatus() == Status.BLOCKED) {
            throw new IllegalStateException("Карта уже заблокирована");
        }
        card.setStatus(Status.BLOCKED);
        cardRepository.save(card);
        return CardDto.builder()
                .maskedCardNumber(card.getMaskedCardNumber())
                .expiryDate(card.getExpiryDate().toString())
                .status(card.getStatus().toString())
                .ownerId(card.getOwnerUser().getIdUser())
                .build();
    }

    /**
     * Активирует заблокированную карту
     *
     * @param cardId Id карты, которую необходимо разблокировать
     * @return CardDto DTO, содержащий в себе строку maskedCardNumber маску карты,
     * строку status статус карты,
     * строку expiryDate срок действия карты
     * числовое значение ownerId ID держателя карты
     *
     * @throws IllegalArgumentException выбрасывается в случае, когда карта числится активной
     */
    public CardDto activateCard(Long cardId) {
        Card card = findCardById(cardId);
        if(card.getStatus() == Status.ACTIVE) {
            throw new IllegalStateException("Карта уже активна");
        }
        card.setStatus(Status.ACTIVE);
        cardRepository.save(card);
        return CardDto.builder()
                .maskedCardNumber(card.getMaskedCardNumber())
                .expiryDate(card.getExpiryDate().toString())
                .status(card.getStatus().toString())
                .ownerId(card.getOwnerUser().getIdUser())
                .build();
    }

    /**
     * Возвращает баланс карты с данным ID
     *
     * @param cardId Id карты
     * @return BigDecimal значение, баланс карты
     *
     */
    public BigDecimal getBalance(Long cardId) {
        Card card = findCardById(cardId);
        return card.getBalance();
    }

    /**
     * Выполняет перевод средств между двумя картами одного владельца
     *
     * @param moneyTransferDTO DTO содержащее в себе cardIdFrom карты, с которой совершается перевод.
     *                         cardIdTo карты, на которую совершается перевод.
     *                         amount сумма перевода
     * @return остаток баланса на карте, с которой совершался перевод
     *
     * @throws BalanceIsNotEnoughException выбрасывается в случае нехватки денег для перевода
     * @throws InvalidTransferAmountException выбрасывается в случае, когда сумма перевода меньше или равна 0
     * @throws CrossUserTransferNotAllowedException выбрасывается в случае, когда владельцы счетов различаются
     * @throws SameAccountTransferException выбрасываются в случае, когда счет-получатель и счет-отправитель одинаковые
     */
    @Transactional
    public BigDecimal transferMoney(MoneyTransferDto moneyTransferDTO) {
        Card cardFrom = findCardById(moneyTransferDTO.getCardIdFrom());
        Card cardTo = findCardById(moneyTransferDTO.getCardIdTo());

        if(!cardFrom.getOwnerUser().equals(cardTo.getOwnerUser())) {
            throw new CrossUserTransferNotAllowedException("Переводы возможны только между счетами одного пользователя");
        }
        if(cardFrom.getBalance().compareTo(moneyTransferDTO.getAmount()) < 0){
            throw new BalanceIsNotEnoughException("На карте не хватает денег для перевода");
        }
        if(moneyTransferDTO.getAmount().compareTo(BigDecimal.ZERO) < 0 ){
            throw new InvalidTransferAmountException("Сумма перевода не может быть 0 или меньше 0");
        }
        if(cardFrom.equals(cardTo)){
            throw new SameAccountTransferException("Счета для перевода должны различаться!");
        }
        cardFrom.setBalance(cardFrom.getBalance().subtract(moneyTransferDTO.getAmount()));
        cardTo.setBalance(cardTo.getBalance().add(moneyTransferDTO.getAmount()));

        return cardFrom.getBalance();
    }
}
