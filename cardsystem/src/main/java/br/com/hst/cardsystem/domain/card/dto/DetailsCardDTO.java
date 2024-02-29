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

import java.time.LocalDate;

public record DetailsCardDTO(
        Long id,
        String numberCard,
        CardBrand cardBrand,
        LocalDate dateExpiration) {

    public DetailsCardDTO(Card card) {
        this(card.getId(), card.getNumberCard(), card.getCardBrand(), card.getDateExpiration());
    }

}
