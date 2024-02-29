/*
 * (c) COPYRIGHT 2023 HST EQUIPAMENTOS
 * ELETRONICOS Ltda, Campinas (SP), Brasil
 * ALL RIGHTS RESERVED - TODOS OS DIREITOS RESERVADOS
 * CONFIDENTIAL, UNPUBLISHED PROPERTY OF HST E. E. Ltda
 * PROPRIEDADE CONFIDENCIAL NAO PUBLICADA DA HST Ltda.
 */
package br.com.hst.cardsystem.domain.card.dto;

import br.com.hst.cardsystem.domain.card.Card;
import br.com.hst.cardsystem.domain.card.CardBrand;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record DetailsCardEncryptedDTO(
        Long id,
        Long userId,
        String originalNumberCard,
        CardBrand cardBrand,
        @JsonFormat(pattern = "MM/yyyy") LocalDate dateExpiration,
        String cvv) {

    public DetailsCardEncryptedDTO(Card card) {
        this(card.getId(), card.getUser().getId() ,card.getOriginalNumberCard(), card.getCardBrand(), card.getDateExpiration(), card.getCvv());
    }

}
