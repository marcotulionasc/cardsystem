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

public record UserByStatusDTO(
        Long id,
        @NotBlank String name,
        @CPF String cpf) {

    public UserByStatusDTO(User user){
        this(user.getId(), user.getName(), user.getCpf());
    }

}
