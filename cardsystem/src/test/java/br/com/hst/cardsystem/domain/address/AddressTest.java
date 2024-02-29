package br.com.hst.cardsystem.domain.address;

import br.com.hst.cardsystem.domain.address.dto.AddressDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AddressTest {

    @Test
    void updateAddressData() {
        AddressDTO initialData = new AddressDTO("Rua Jose Moises de Nazar", "209",
                "Jardim São Fernando", "13100350", UF.SP, "Casa");

        AddressDTO updatedData = initialData;

        Assertions.assertEquals("Rua Jose Moises de Nazar", updatedData.street());
        Assertions.assertEquals("209", updatedData.numberAddress());
        Assertions.assertEquals("Jardim São Fernando", updatedData.neighborhood());
        Assertions.assertEquals("13100350", updatedData.cep());
        Assertions.assertEquals(UF.SP, updatedData.uf());
        Assertions.assertEquals("Casa", updatedData.complement());
    }

}