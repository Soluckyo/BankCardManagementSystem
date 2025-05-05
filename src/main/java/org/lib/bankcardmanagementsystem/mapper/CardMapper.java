package org.lib.bankcardmanagementsystem.mapper;

import org.lib.bankcardmanagementsystem.dto.CardDto;
import org.lib.bankcardmanagementsystem.entity.Card;

public class CardMapper {
    public static CardDto toCardDto(Card card) {
        CardDto dto = new CardDto();
        dto.setCardId(card.getIdCard());
        dto.setMaskedCardNumber(card.getMaskedCardNumber());
        dto.setOwnerId(card.getOwnerUser().getIdUser());
        dto.setStatus(card.getStatus().toString());
        dto.setExpiryDate(card.getExpiryDate().toString());
        dto.setBalance(card.getBalance());
        return dto;
    }
}
