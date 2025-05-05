package org.lib.bankcardmanagementsystem.service;

import jakarta.transaction.Transactional;
import org.lib.bankcardmanagementsystem.dto.CardDto;
import org.lib.bankcardmanagementsystem.dto.MoneyTransferDto;
import org.lib.bankcardmanagementsystem.entity.Card;
import org.lib.bankcardmanagementsystem.entity.CardSpecification;
import org.lib.bankcardmanagementsystem.entity.Status;
import org.lib.bankcardmanagementsystem.entity.User;
import org.lib.bankcardmanagementsystem.exception.BalanceIsNotEnoughException;
import org.lib.bankcardmanagementsystem.exception.CardBlockedException;
import org.lib.bankcardmanagementsystem.exception.CardCreationException;
import org.lib.bankcardmanagementsystem.exception.CardNotFoundException;
import org.lib.bankcardmanagementsystem.exception.CrossUserTransferNotAllowedException;
import org.lib.bankcardmanagementsystem.exception.InvalidAccessTokenException;
import org.lib.bankcardmanagementsystem.exception.InvalidAuthHeaderException;
import org.lib.bankcardmanagementsystem.exception.InvalidTransferAmountException;
import org.lib.bankcardmanagementsystem.exception.SameAccountTransferException;
import org.lib.bankcardmanagementsystem.mapper.CardMapper;
import org.lib.bankcardmanagementsystem.repository.CardRepository;
import org.lib.bankcardmanagementsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Сервис для работы с картами
 */
@Service
public class CardService implements ICardService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    /**
     *  Срок действия карты в годах
     *  Значение загружается из application.properties по ключу `card.expiry.year`.
     */
    @Value("${card.expiry.year}")
    private Long CARD_EXPIRY_YEAR;

    /**
     *  Стартовый баланс карты при создании
     *  Значение загружается из application.properties по ключу `card.start.balance`.
     */
    @Value("${card.start.balance}")
    private BigDecimal START_BALANCE;

    private final CardRepository cardRepository;
    private final CardNumberGenerator cardNumberGenerator;
    private final UserService userService;

    public CardService(CardRepository cardRepository, CardNumberGenerator cardNumberGenerator, UserService userService, UserRepository userRepository, JwtService jwtService) {
        this.cardRepository = cardRepository;
        this.cardNumberGenerator = cardNumberGenerator;
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    /**
     * Возвращает все карты постранично
     *
     * @param pageable параметры страницы
     * @return страница с картами
     * @throws CardNotFoundException выбрасывается, если карты не найдены
     */
    public Page<CardDto> getAllCards(Pageable pageable) {
        Page<Card> cards = cardRepository.findAll(pageable);
        return cards.map(CardMapper::toCardDto);
    }

    /**
     * Возвращает все карты, принадлежащие одному пользователю.
     * Метод может работать с параметризованной фильтрацией
     *
     * @param pageable параметры страницы
     * @throws CardNotFoundException выбрасывается, если карты не найдены
     * @return страница с картами
     */
    public Page<CardDto> getAllCardsByOwnerId(String authHeader, Pageable pageable, BigDecimal minBalance, Status status) {
        String email = extractUserInfoFromToken(authHeader);
        User user = userService.getUserByEmail(email);

        Specification<Card> spec = Specification
                .where(CardSpecification.hasOwnerId(user.getIdUser()))
                .and(CardSpecification.hasStatus(status))
                .and(CardSpecification.hasMinBalance(minBalance));

        Page<Card> cards = cardRepository.findAll(spec, pageable);
        return cards.map(CardMapper::toCardDto);
    }


    /**
     * Берет токен из заголовка, достает из токена email и возвращает его
     *
     *
     * @param authHeader auth заголовок из которого будет взят токен
     * @throws InvalidAuthHeaderException выбрасывается, если заголовок авторизации не содержит токен
     * @throws InvalidAccessTokenException выбрасывается, если токен валидируется
     * @return строку с email
     */
    public String extractUserInfoFromToken(String authHeader){
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidAuthHeaderException("Недопустимый заголовок авторизации!");
        }

        String token = authHeader.substring(7);

        if(!jwtService.validateToken(token)) {
            throw new InvalidAccessTokenException("Недействительный или просроченный токен");
        }

         return jwtService.getEmailFromToken(token);
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
     * Создает новую карту с стартовым балансом START_BALANCE и сроком годности CARD_EXPIRY_YEARS
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
                    .expiryDate(LocalDate.now().plusYears(CARD_EXPIRY_YEAR))
                    .status(Status.ACTIVE)
                    .balance(START_BALANCE)
                    .build();

            cardRepository.save(card);

            return CardMapper.toCardDto(card);
        }catch (Exception e) {
            e.printStackTrace();
            throw new CardCreationException("Ошибка при создании карты");
        }
    }

    /**
     * Удаляет карту по её ID. Только для администратора.
     * (Вообще лучше сделать не жесткое удаление, а архивацию а-ля "мягкое удаление", но тут я решил сделать так)
     *
     * @param cardId ID карты
     * @throws CardNotFoundException выбрасывается, если карта не найдена
     */
    public void deleteCardById(Long cardId) {
        Card card = findCardById(cardId);

        cardRepository.delete(card);
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
        if(card.getStatus() == Status.BLOCKED){
            throw new IllegalArgumentException("Карта уже заблокирована");
        }
        card.setStatus(Status.BLOCKED);
        card.setBlockRequest(false);
        cardRepository.save(card);
        return CardMapper.toCardDto(card);
    }

    /**
     *
     *
     * @param authHeader заголовок запроса
     * @param cardId ID карты, которую необходимо заблокировать
     * @return строка
     */
    public String requestForBlockCard(String authHeader, Long cardId) {
        String email = extractUserInfoFromToken(authHeader);
        User user = userService.getUserByEmail(email);
        Card card = findCardById(cardId);
        if (!Objects.equals(card.getOwnerUser().getIdUser(), user.getIdUser())) {
            throw new AccessDeniedException("Нельзя запросить блокировку чужой карты");
        }

        card.setBlockRequest(true);
        cardRepository.save(card);
        return "Запрос на блокировку карты отправлен";
    }

    /**
     * Возвращает все карты, которые необходимо заблокировать
     *
     *
     * @param pageable параметры страницы
     * @return Страница с DTO карт
     */
    public Page<CardDto> findRequestCardBlock(Pageable pageable) {
        Page<Card> cards = cardRepository.findByBlockRequest(true, pageable);
        return cards.map(CardMapper::toCardDto);
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
        return CardMapper.toCardDto(card);
    }

    /**
     * Возвращает баланс карты с данным ID
     *
     * @param authHeader заголовок, получаем из запроса
     * @param cardId Id карты
     * @throws AccessDeniedException выбрасывается, если пользователь не является держателем карты
     * @return BigDecimal значение, баланс карты
     *
     */
    public BigDecimal getBalance(String authHeader, Long cardId) {
        String email = extractUserInfoFromToken(authHeader);
        User user = userService.getUserByEmail(email);
        Card card = findCardById(cardId);

        if (!Objects.equals(card.getOwnerUser().getIdUser(), user.getIdUser())) {
            throw new AccessDeniedException("Нельзя запросить баланс чужой карты");
        }

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
     * @throws CardBlockedException выбрасывается в случае, когда обе или одна карты заблокированы
     * @throws AccessDeniedException выбрасывается, если пользователь не является держателем карты
     */
    @Transactional
    public BigDecimal transferMoney(String authHeader, MoneyTransferDto moneyTransferDTO) {
        Card cardFrom = findCardById(moneyTransferDTO.getCardIdFrom());
        Card cardTo = findCardById(moneyTransferDTO.getCardIdTo());
        String email = extractUserInfoFromToken(authHeader);
        User user = userService.getUserByEmail(email);

        if(!cardFrom.getOwnerUser().equals(cardTo.getOwnerUser())) {
            throw new CrossUserTransferNotAllowedException("Переводы возможны только между счетами одного пользователя");
        }

        if(cardFrom.getBalance().compareTo(moneyTransferDTO.getAmount()) < 0){
            throw new BalanceIsNotEnoughException("На карте не хватает денег для перевода");
        }

        if(moneyTransferDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0 ){
            throw new InvalidTransferAmountException("Сумма перевода не может быть 0 или меньше 0");
        }

        if(cardFrom.equals(cardTo)){
            throw new SameAccountTransferException("Счета для перевода должны различаться!");
        }

        if(cardFrom.getStatus() == Status.BLOCKED || cardTo.getStatus() == Status.BLOCKED){
            throw new CardBlockedException("Карты не должны быть заблокированы!");
        }

        if (!Objects.equals(cardFrom.getOwnerUser().getIdUser(), user.getIdUser())) {
            throw new AccessDeniedException("Нельзя выполнить перевод с чужой карты");
        }

        cardFrom.setBalance(cardFrom.getBalance().subtract(moneyTransferDTO.getAmount()));
        cardTo.setBalance(cardTo.getBalance().add(moneyTransferDTO.getAmount()));

        return cardFrom.getBalance();
    }
}
