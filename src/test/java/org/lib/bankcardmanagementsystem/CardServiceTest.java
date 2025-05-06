package org.lib.bankcardmanagementsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lib.bankcardmanagementsystem.dto.CardDto;
import org.lib.bankcardmanagementsystem.dto.MoneyTransferDto;
import org.lib.bankcardmanagementsystem.entity.Card;
import org.lib.bankcardmanagementsystem.entity.Status;
import org.lib.bankcardmanagementsystem.entity.User;
import org.lib.bankcardmanagementsystem.exception.*;
import org.lib.bankcardmanagementsystem.repository.CardRepository;
import org.lib.bankcardmanagementsystem.repository.UserRepository;
import org.lib.bankcardmanagementsystem.service.CardNumberGenerator;
import org.lib.bankcardmanagementsystem.service.CardService;
import org.lib.bankcardmanagementsystem.service.JwtService;
import org.lib.bankcardmanagementsystem.service.UserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardNumberGenerator cardNumberGenerator;

    @InjectMocks
    private CardService cardService;

    private User user;
    private Card card;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setIdUser(1L);
        user.setEmail("test@example.com");

        card = Card.builder()
                .idCard(1L)
                .ownerUser(user)
                .maskedCardNumber("**** **** **** 1234")
                .encryptedCardNumber("encrypted")
                .expiryDate(LocalDate.now().plusYears(3))
                .status(Status.ACTIVE)
                .balance(BigDecimal.valueOf(1000))
                .build();

        cardService = new CardService(cardRepository, cardNumberGenerator, userService, userRepository, jwtService, BigDecimal.valueOf(1000), 5L);
    }

    @Test
    void createCardSuccess() {
        User mockUser = new User();
        mockUser.setIdUser(1L);

        String generatedNumber = "1234567812345678";
        String encryptedNumber = "enc123";
        String maskedNumber = "**** **** **** 5678";

        when(userService.getUserById(1L)).thenReturn(mockUser);
        when(cardNumberGenerator.generateCardNumber()).thenReturn(generatedNumber);
        when(cardNumberGenerator.encryptedNumberCard(generatedNumber)).thenReturn(encryptedNumber);
        when(cardNumberGenerator.maskedNumberCard(generatedNumber)).thenReturn(maskedNumber);
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CardDto result = cardService.createCard(1L);

        assertNotNull(result);
        assertEquals(maskedNumber, result.getMaskedCardNumber());
        assertEquals(Status.ACTIVE.name(), result.getStatus());
        assertEquals(1L, result.getOwnerId());
    }

    @Test
    void testExtractEmailInfoFromTokenValidToken() {
        when(jwtService.validateToken("token")).thenReturn(true);
        when(jwtService.getEmailFromToken("token")).thenReturn("test@example.com");

        String email = cardService.extractEmailInfoFromToken("Bearer token");

        assertEquals("test@example.com", email);
    }

    @Test
    void testExtractEmailInfoFromTokenInvalidHeader() {
        assertThrows(InvalidAuthHeaderException.class, () -> cardService.extractEmailInfoFromToken("InvalidHeader"));
    }

    @Test
    void testExtractEmailInfoFromTokenInvalidToken() {
        when(jwtService.validateToken("token")).thenReturn(false);

        assertThrows(InvalidAccessTokenException.class, () -> cardService.extractEmailInfoFromToken("Bearer token"));
    }

    @Test
    void testFindCardByIdCardExists() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        Card result = cardService.findCardById(1L);

        assertEquals(card, result);
    }

    @Test
    void testFindCardByIdCardNotFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.findCardById(1L));
    }

    @Test
    void testBlockedCardSuccess() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        card.setStatus(Status.ACTIVE);

        CardDto result = cardService.blockedCard(1L);

        assertEquals("BLOCKED", result.getStatus());
    }

    @Test
    void testBlockedCardAlreadyBlocked() {
        card.setStatus(Status.BLOCKED);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        assertThrows(IllegalArgumentException.class, () -> cardService.blockedCard(1L));
    }

    @Test
    void testActivateCardSuccess() {
        card.setStatus(Status.BLOCKED);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        CardDto result = cardService.activateCard(1L);

        assertEquals("ACTIVE", result.getStatus());
    }

    @Test
    void testActivateCardAlreadyActive() {
        card.setStatus(Status.ACTIVE);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        assertThrows(IllegalStateException.class, () -> cardService.activateCard(1L));
    }

    @Test
    void testGetBalanceSuccess() {
        when(jwtService.validateToken("token")).thenReturn(true);
        when(jwtService.getEmailFromToken("token")).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        BigDecimal balance = cardService.getBalance("Bearer token", 1L);

        assertEquals(card.getBalance(), balance);
    }

    @Test
    void testGetBalanceAccessDenied() {
        User anotherUser = new User();
        anotherUser.setIdUser(2L);
        card.setOwnerUser(anotherUser);

        when(jwtService.validateToken("token")).thenReturn(true);
        when(jwtService.getEmailFromToken("token")).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        assertThrows(AccessDeniedException.class, () -> cardService.getBalance("Bearer token", 1L));
    }

    @Test
    void testTransferMoneySuccess() {
        Card cardTo = Card.builder()
                .idCard(2L)
                .ownerUser(user)
                .status(Status.ACTIVE)
                .balance(BigDecimal.valueOf(100))
                .build();

        MoneyTransferDto dto = new MoneyTransferDto(1L, 2L, BigDecimal.valueOf(500));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(cardTo));
        when(jwtService.validateToken("token")).thenReturn(true);
        when(jwtService.getEmailFromToken("token")).thenReturn(user.getEmail());
        when(userService.getUserByEmail(user.getEmail())).thenReturn(user);

        BigDecimal result = cardService.transferMoney("Bearer token", dto);

        assertEquals(BigDecimal.valueOf(500), result);
        assertEquals(BigDecimal.valueOf(600), cardTo.getBalance());
    }

    @Test
    void testFindRequestCardBlock() {
        when(cardRepository.findByBlockRequest(eq(true), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(card)));

        Page<CardDto> result = cardService.findRequestCardBlock(Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
    }

}

