package org.lib.bankcardmanagementsystem.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Класс для генерации шифрования и создания маски номера карты
 */
@Component
public class CardNumberGenerator {

    @Value("${card.bin}")
    private String BIN;

    @Value("${card.secret.key}")
    private String SECRET_KEY;

    @Value("${card.secret.algorithm}")
    private String ALGORITHM;

    /**
     * Генерирует номер карты, соответствующий алгоритму Луна.
     * BIN задаётся как префикс, к которому добавляются случайные цифры и контрольная сумма.
     *
     * @return строка с валидным номером карты
     */
    public String generateCardNumber() {
        StringBuilder cardNumber = new StringBuilder(BIN);
        for(int i = 0; i < 9; i++) {
            cardNumber.append((int) (Math.random() * 10));
        }
        int checkDigit = getLuhnCheckDigit(cardNumber.toString());
        cardNumber.append(checkDigit);
        return cardNumber.toString();
    }

    /**
     * Метод генерирует последнюю цифру для номера карты с помощью алгоритма Luhn
     *
     * @param number строка, представляющая номер карты без контрольной цифры
     * @return последняя цифра для номера карты
     */
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

    /**
     * Метод шифрует номер карты.
     * Использует алгоритм ALGORITHM и ключ SECRET_KEY
     *
     * @param cardNumber строка, представляющая собой незашифрованный номер карты
     * @return зашифрованный номер карты
     * @throws RuntimeException выбрасывается в случае ошибки шифрования карты
     */
    public String encryptedNumberCard(String cardNumber) {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        System.out.println("Key length: " + keyBytes.length); // Проверяем длину ключа
        try{
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(cardNumber.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        }catch(Exception e){
            throw new RuntimeException("Ошибка при шифровании карты!", e);
        }
    }

    /**
     * Метод создает маску номера карты.
     *
     * @param cardNumber строка, представляющая собой незашифрованный номер карты
     * @return строка с маской номера карты
     */
    public String maskedNumberCard(String cardNumber) {
        if(cardNumber == null || cardNumber.length() < 4){
            return "****";
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
