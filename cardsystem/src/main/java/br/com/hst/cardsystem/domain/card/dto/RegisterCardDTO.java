/*
 * (c) COPYRIGHT 2023 HST EQUIPAMENTOS
 * ELETRONICOS Ltda, Campinas (SP), Brasil
 * ALL RIGHTS RESERVED - TODOS OS DIREITOS RESERVADOS
 * CONFIDENTIAL, UNPUBLISHED PROPERTY OF HST E. E. Ltda
 * PROPRIEDADE CONFIDENCIAL NAO PUBLICADA DA HST Ltda.
 */
package br.com.hst.cardsystem.domain.card.dto;

import br.com.hst.cardsystem.domain.card.CardBrand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RegisterCardDTO(

        @NotBlank @Pattern(regexp = "^\\d{4} \\d{4} \\d{4} \\d{4}$",
                message = "Card number is invalid, must contain 16 numbers.")
        @Schema(description = "Number card", example = "5443 3840 8887 8311")
        String numberCard,

        @Schema(description = "Card brand", example = "VISA")
        @NotNull CardBrand cardBrand,

        @Schema(description = "CVV card", example = "123")
        @NotBlank String cvv,

        @Schema(description = "User id", example = "1")
        @NotNull Long userId) {

}
