package org.lib.bankcardmanagementsystem.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class CardNumberGenerator {

    @Value("${card.bin}")
    private String BIN;

    @Value("${card.secret.key}")
    private String SECRET_KEY;

    @Value("${card.secret.algorithm}")
    private String ALGORITHM;

    //метод генерирует номер карты в соответствии со стандартом по алгоритму Luhn
    public String generateCardNumber() {
        StringBuilder cardNumber = new StringBuilder(BIN);
        for(int i = 0; i < 9; i++) {
            cardNumber.append((int) (Math.random() * 10));
        }
        int checkDigit = getLuhnCheckDigit(cardNumber.toString());
        cardNumber.append(checkDigit);
        return cardNumber.toString();
    }

    //метод генерирует последнюю цифру для номера карты с помощью алгоритма Luhn
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

    //шифрование номера карты(безопасность)
    public String encryptCardNumber(String encryptNumber) {
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(encryptNumber.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        }catch(Exception e){
            throw new RuntimeException("Ошибка при шифровании карты!", e);
        }
    }

    //создание маски номера карты(безопасность)
    public String maskCardNumber(String maskedNumber) {
        if(maskedNumber == null || maskedNumber.length() < 4){
            return "****";
        }
        return "**** **** **** " + maskedNumber.substring(maskedNumber.length() - 4);
    }
}
