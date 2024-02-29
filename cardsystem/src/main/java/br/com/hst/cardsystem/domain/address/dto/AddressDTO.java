/*
 * (c) COPYRIGHT 2023 HST EQUIPAMENTOS
 * ELETRONICOS Ltda, Campinas (SP), Brasil
 * ALL RIGHTS RESERVED - TODOS OS DIREITOS RESERVADOS
 * CONFIDENTIAL, UNPUBLISHED PROPERTY OF HST E. E. Ltda
 * PROPRIEDADE CONFIDENCIAL NAO PUBLICADA DA HST Ltda.
 */
package br.com.hst.cardsystem.domain.address.dto;

import br.com.hst.cardsystem.domain.address.UF;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddressDTO(

        @NotBlank @Schema(description = "Street", example = "Rua José Inácio da Silva")
        String street,

        @Schema(description = "Number address", example = "622")
        String numberAddress,

        @NotBlank @Schema(description = "Neighborhood", example = "Parque das Árvores")
        String neighborhood,

        @NotBlank @Pattern(regexp = "\\d{8}") @Schema(description = "CEP", example = "69309030")
        String cep,

        UF uf,

        @Schema(description = "Complement", example = "Apartamento 84B")
        String complement) {
}
