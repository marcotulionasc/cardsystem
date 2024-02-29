/*
 * (c) COPYRIGHT 2023 HST EQUIPAMENTOS
 * ELETRONICOS Ltda, Campinas (SP), Brasil
 * ALL RIGHTS RESERVED - TODOS OS DIREITOS RESERVADOS
 * CONFIDENTIAL, UNPUBLISHED PROPERTY OF HST E. E. Ltda
 * PROPRIEDADE CONFIDENCIAL NAO PUBLICADA DA HST Ltda.
 */
package br.com.hst.cardsystem.domain.user.dto;

import br.com.hst.cardsystem.domain.user.User;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;

public record UserBySalaryDTO(
        @NotBlank String name,
        @CPF String cpf,
        @NotBlank String occupation,
        BigDecimal salary) {

    public UserBySalaryDTO(User user){
        this(user.getName(), user.getCpf(), user.getOccupation(), user.getSalary());
    }

}
