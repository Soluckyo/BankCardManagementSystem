package org.lib.bankcardmanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.lib.bankcardmanagementsystem.dto.CardDto;
import org.lib.bankcardmanagementsystem.dto.MoneyTransferDto;
import org.lib.bankcardmanagementsystem.entity.Status;
import org.lib.bankcardmanagementsystem.service.ICardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/card")
public class CardController {

    private final ICardService cardService;

    public CardController(ICardService cardService) {
        this.cardService = cardService;
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Карты не найдены"),
            @ApiResponse(responseCode = "200", description = "Карты успешно найдены"),
            @ApiResponse(responseCode = "403", description = "Доступ к методу только у администратора!")
    })
    @Operation(
            summary = "Получение всех карт",
            description = "Возвращает все существующие карты. Только для администратора!"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/only-admin")
    public ResponseEntity<Page<CardDto>> getAllCards(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(cardService.getAllCards(pageable));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Карты или пользователь не найдены"),
            @ApiResponse(responseCode = "200", description = "Карты успешно найдены"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "401", description = "Доступ запрещён, токен отсутствует или не валиден")
    })
    @Operation(
            summary = "Получение всех карт пользователя",
            description = "Берет AuthHeader из запроса устанавливает " +
                    "через токен пользователя и возвращает все принадлежащие ему карты"
    )
    @GetMapping("/all")
    public ResponseEntity<Page<CardDto>> getAllCardsByOwnerId(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) BigDecimal minBalance,
            @RequestParam(required = false) Status status

    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(cardService.getAllCardsByOwnerId(authHeader, pageable, minBalance, status));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта успешно создана"),
            @ApiResponse(responseCode = "403", description = "Только для администратора!"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Ошибка создания карты")
    })
    @Operation(
            summary = "Создание новой карты",
            description = "Принимает ID пользователя из пути и создает новую карту. Только для администратора!"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{ownerId}/create-card")
    public ResponseEntity<CardDto> createCard(@PathVariable Long ownerId) {
        return ResponseEntity.ok(cardService.createCard(ownerId));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Карта не найдена"),
            @ApiResponse(responseCode = "200", description = "Карта успешно заблокирована"),
            @ApiResponse(responseCode = "403", description = "Доступ к методу только у администратора!")
    })
    @Operation(
            summary = "Блокировка карты",
            description = "Принимает ID карты и ставит статус BLOCKED. Только для администратора!"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{cardId}/blocked")
    public ResponseEntity<CardDto> blockedCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.blockedCard(cardId));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Карта не найдена"),
            @ApiResponse(responseCode = "200", description = "Запрос на блокировку успешно отправлен")
    })
    @Operation(
            summary = "Запрос на блокировку карты",
            description = "Принимает ID карты и ставит флаг запроса блокировки true"
    )
    @PostMapping("/{cardId}/block-request")
    public ResponseEntity<String> responseForBlockedCard(@RequestHeader("Authorization") String authHeader, Long cardId) {
        return ResponseEntity.ok(cardService.requestForBlockCard(authHeader, cardId));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Карта не найдена"),
            @ApiResponse(responseCode = "200", description = "Карта успешно заблокирована"),
            @ApiResponse(responseCode = "403", description = "Доступ к методу только у администратора!")
    })
    @Operation(
            summary = "Блокировка карты",
            description = "Принимает ID карты и ставит статус BLOCKED. Только для администратора!"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all-block-request")
    public Page<CardDto> findRequestCardBlock(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "5") int size){
        Pageable pageable = PageRequest.of(page, size);
        return cardService.findRequestCardBlock(pageable);
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Карта не найдена"),
            @ApiResponse(responseCode = "200", description = "Карта успешно активирована"),
            @ApiResponse(responseCode = "403", description = "Доступ к методу только у администратора!")
    })
    @Operation(
            summary = "Активация карты",
            description = "Принимает ID карты и ставит статус ACTIVE. Только для администратора!"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{cardId}/activate")
    public ResponseEntity<CardDto> activateCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.activateCard(cardId));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Карта не найдена"),
            @ApiResponse(responseCode = "200", description = "Запрос успешно выполнен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @Operation(
            summary = "Получить баланс карты",
            description = "Принимает ID карты из пути и возвращает баланс этой карты"
    )
    @GetMapping("/{cardId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.getBalance(cardId));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Карта не найдена"),
            @ApiResponse(responseCode = "200", description = "Перевод успешно выполнен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "409", description = "Не корректные данные счетов"),
            @ApiResponse(responseCode = "400", description = "Сумма перевода не корректна")
    })
    @Operation(
            summary = "Перевод средств с одного счета на другой",
            description = "Принимает MoneyTransferDto и выполняет перевод средств. Счета должны принадлежать одному пользователю!"
    )
    @PutMapping("/transfer")
    public ResponseEntity<BigDecimal> moneyTransfer(@RequestBody MoneyTransferDto moneyTransferDto) {
        return ResponseEntity.ok(cardService.transferMoney(moneyTransferDto));
    }
}
