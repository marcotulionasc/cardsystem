/*
 * (c) COPYRIGHT 2023 HST EQUIPAMENTOS
 * ELETRONICOS Ltda, Campinas (SP), Brasil
 * ALL RIGHTS RESERVED - TODOS OS DIREITOS RESERVADOS
 * CONFIDENTIAL, UNPUBLISHED PROPERTY OF HST E. E. Ltda
 * PROPRIEDADE CONFIDENCIAL NAO PUBLICADA DA HST Ltda.
 */
package br.com.hst.cardsystem.domain.user.dto;

import br.com.hst.cardsystem.domain.address.Address;
import br.com.hst.cardsystem.domain.user.CivilStatus;
import br.com.hst.cardsystem.domain.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;

public record DetailsUserDTO(
        Long id,
        @NotBlank
        String name,
        @CPF
        String cpf,
        @NotBlank @Pattern(regexp = "^\\(\\d{2}\\) \\d{4,5}-\\d{4}$",
                message = "The contact number must be in the format XX XXXXX-XXXX or XX XXXXXXXXX.")
        String contactNumber,
        CivilStatus civilStatus,
        @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/(19\\d\\d|20[01]\\d|202[0-2])$",
                message = "Birthdate must be in the format dd/mm/yyyy.")
        String birthdate,
        @NotBlank String occupation,
        BigDecimal salary,
        boolean currentlyActive,
        @Valid Address address) {

    public DetailsUserDTO(User user) {
        this(user.getId(),
                user.getName(), user.getCpf(),user.getContactNumber(), user.getCivilStatus(),
                user.getBirthdate(), user.getOccupation(), user.getSalary(), user.isCurrentlyActive(), user.getAddress());
    }

}

