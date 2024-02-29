/*
 * (c) COPYRIGHT 2023 HST EQUIPAMENTOS
 * ELETRONICOS Ltda, Campinas (SP), Brasil
 * ALL RIGHTS RESERVED - TODOS OS DIREITOS RESERVADOS
 * CONFIDENTIAL, UNPUBLISHED PROPERTY OF HST E. E. Ltda
 * PROPRIEDADE CONFIDENCIAL NAO PUBLICADA DA HST Ltda.
 */
package br.com.hst.cardsystem.domain.user.dto;

import br.com.hst.cardsystem.domain.address.dto.AddressDTO;
import br.com.hst.cardsystem.domain.user.CivilStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;

@Schema(name = "RegisterUserDTO")
public record RegisterUserDTO(

        @NotBlank @Schema(description = "User name", example = "João da Silva")
        String name,

        @NotBlank @CPF @Schema(description = "User cpf", example = "387.293.062-13")
        String cpf,

        @NotBlank @Schema(description = "User contact number", example = "(19) 98866-1339")
        String contactNumber,

        CivilStatus civilStatus,

        @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/(19\\d\\d|20[01]\\d|202[0-2])$",
                message = "Birthdate must be in the format dd/mm/yyyy.")
        @Schema(description = "User birthdate", example = "13/08/1980")
        String birthdate,

        @NotBlank @Schema(description = "User occupation", example = "Software developer")
        String occupation,

        @Schema(description = "User salary", example = "2500")
        BigDecimal salary,

        @Valid
        AddressDTO address
) {
}
