package br.com.hst.cardsystem.domain.card;

import br.com.hst.cardsystem.domain.card.dto.RegisterCardDTO;
import br.com.hst.cardsystem.domain.card.dto.UpdateCardDTO;
import br.com.hst.cardsystem.domain.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

public class CardTest {

    private Card card;
    private User user;


    @BeforeEach
    void setUp() {
        user = Mockito.mock(User.class);
        card = new Card();
        card.setUser(user);
    }

    @Test // Endpoint of registerCard
    void testCreateCardFromRegisterCardDTO() {
        RegisterCardDTO registerCardDTO = new RegisterCardDTO("1234567890123456", CardBrand.VISA, "123",(long)1) ;

        Card newCard = new Card(registerCardDTO);

        Assertions.assertEquals("1234567890123456", newCard.getNumberCard());
        Assertions.assertEquals(CardBrand.VISA, newCard.getCardBrand());
        Assertions.assertEquals(LocalDate.now().plusYears(2), newCard.getDateExpiration());
        Assertions.assertEquals("123", newCard.getCvv());
    }

    @Test //Endpoint of updateCard
    void testUpdateCardData() {
        UpdateCardDTO updateCardDTO = new UpdateCardDTO("9876543210987654", CardBrand.MASTERCARD,LocalDate.now().plusYears(3), "456");

        card.updateCardData(updateCardDTO);

        Assertions.assertEquals("9876543210987654", card.getNumberCard());
        Assertions.assertEquals(CardBrand.MASTERCARD, card.getCardBrand());
        Assertions.assertEquals(LocalDate.now().plusYears(3), card.getDateExpiration());
        Assertions.assertEquals("456", card.getCvv());
    }

}
