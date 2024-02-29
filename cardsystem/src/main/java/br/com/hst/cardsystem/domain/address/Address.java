/*
 * (c) COPYRIGHT 2023 HST EQUIPAMENTOS
 * ELETRONICOS Ltda, Campinas (SP), Brasil
 * ALL RIGHTS RESERVED - TODOS OS DIREITOS RESERVADOS
 * CONFIDENTIAL, UNPUBLISHED PROPERTY OF HST E. E. Ltda
 * PROPRIEDADE CONFIDENCIAL NAO PUBLICADA DA HST Ltda.
 */
package br.com.hst.cardsystem.domain.address;

import br.com.hst.cardsystem.domain.address.dto.AddressDTO;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String street;
    private String numberAddress;
    private String neighborhood;
    private String cep;
    private UF uf;
    private String complement;

    public Address(AddressDTO data) {
        this.street = data.street();
        this.numberAddress = data.numberAddress();
        this.neighborhood = data.neighborhood();
        this.cep = data.cep();
        this.uf = data.uf();
        this.complement = data.complement();
    }

    public void updateAddressData(AddressDTO data) {
        this.street = (data.street() != null) ? data.street() : this.street;
        this.numberAddress = (data.numberAddress() != null) ? data.numberAddress() : this.numberAddress;
        this.neighborhood = (data.neighborhood() != null) ? data.neighborhood() : this.neighborhood;
        this.cep = (data.cep() != null) ? data.cep() : this.cep;
        this.uf = (data.uf() != null) ? data.uf() : this.uf;
        this.complement = (data.complement() != null) ? data.complement() : this.complement;
    }
}
