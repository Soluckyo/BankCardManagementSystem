package org.lib.bankcardmanagementsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lib.bankcardmanagementsystem.service.CardNumberGenerator;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CardNumberGeneratorTest {

    private CardNumberGenerator generator;

    @BeforeEach
    void setUp() throws Exception {
        generator = new CardNumberGenerator();

        // Задаём значения полей через рефлексию
        setPrivateField("BIN", "123456");
        setPrivateField("SECRET_KEY", "1234567812345678"); // AES требует 16 байт
        setPrivateField("ALGORITHM", "AES");
    }

    private void setPrivateField(String fieldName, String value) throws Exception {
        Field field = CardNumberGenerator.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(generator, value);
    }

    @Test
    void testGenerateCardNumberSuccess() {
        String cardNumber = generator.generateCardNumber();
        assertNotNull(cardNumber);
        assertEquals(16, cardNumber.length());
        assertTrue(cardNumber.startsWith("123456"));
    }

    @Test
    void testEncryptedNumberCardSuccess() {
        String plainCardNumber = "1234567890123456";
        String encrypted = generator.encryptedNumberCard(plainCardNumber);
        assertNotNull(encrypted);
        assertNotEquals(plainCardNumber, encrypted);
    }

    @Test
    void testMaskedNumberCardSuccess() {
        String masked = generator.maskedNumberCard("1234567890123456");
        assertEquals("**** **** **** 3456", masked);
    }

    @Test
    void testMaskedNumberCardWithShortInput() {
        String masked = generator.maskedNumberCard("123");
        assertEquals("****", masked);
    }

    @Test
    void testEncryptedNumberCardExceptionWithInvalidKey() throws Exception {
        setPrivateField("SECRET_KEY", "invalidkey");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> generator.encryptedNumberCard("1234567890123456"));

        assertTrue(exception.getMessage().contains("Ошибка при шифровании карты"));
    }
}
