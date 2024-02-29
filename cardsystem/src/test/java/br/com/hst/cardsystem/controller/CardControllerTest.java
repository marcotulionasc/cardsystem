/*
 * (c) COPYRIGHT 2023 HST EQUIPAMENTOS
 * ELETRONICOS Ltda, Campinas (SP), Brasil
 * ALL RIGHTS RESERVED - TODOS OS DIREITOS RESERVADOS
 * CONFIDENTIAL, UNPUBLISHED PROPERTY OF HST E. E. Ltda
 * PROPRIEDADE CONFIDENCIAL NAO PUBLICADA DA HST Ltda.
 */
package br.com.hst.cardsystem.controller;

import br.com.hst.cardsystem.domain.address.UF;
import br.com.hst.cardsystem.domain.address.dto.AddressDTO;
import br.com.hst.cardsystem.domain.card.Card;
import br.com.hst.cardsystem.domain.card.CardBrand;
import br.com.hst.cardsystem.domain.card.CardRepository;
import br.com.hst.cardsystem.domain.card.dto.DetailsCardDTO;
import br.com.hst.cardsystem.domain.card.dto.RegisterCardDTO;
import br.com.hst.cardsystem.domain.card.dto.UpdateCardDTO;
import br.com.hst.cardsystem.domain.user.CivilStatus;
import br.com.hst.cardsystem.domain.user.User;
import br.com.hst.cardsystem.domain.user.UserRepository;
import br.com.hst.cardsystem.domain.user.dto.RegisterUserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CardControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CardRepository cardRepository;

    @MockBean
    private UserRepository userRepository;

    @InjectMocks
    private CardController cardController;

    @InjectMocks
    private UserController userController;

    User userCreated = new User(new RegisterUserDTO(
            "João da Silva",
            "12345678901",
            "123-456-7890",
            CivilStatus.MARRIED,
            "01/01/1990",
            "Engineer",
            BigDecimal.valueOf(60000),
            new AddressDTO(
                    "Rua Jose Moises",
                    "123",
                    "Jardim São Fernando",
                    "13100350",
                    UF.SP,
                    "Casa"
            )
    ));

    @Test
    @WithMockUser
    @DisplayName("It should return 200 status and the list of cards with the requested brand")
    public void testListOfCardsByBrand_Success() throws Exception {
        List<RegisterCardDTO> cards = new ArrayList<>();
        cards.add(new RegisterCardDTO("12345678910", CardBrand.VISA, "123", 1L));
        cards.add(new RegisterCardDTO("9876543210", CardBrand.VISA, "456", 2L));
        cards.add(new RegisterCardDTO("123321456789", CardBrand.MASTERCARD, "332", 2L));

        var dateExpiration = LocalDate.of(2025, 10, 24);

        List<DetailsCardDTO> detailsCards = cards.stream()
                .map(cardDTO -> new DetailsCardDTO(
                        null,
                        cardDTO.numberCard(),
                        cardDTO.cardBrand(),
                        dateExpiration))
                .collect(Collectors.toList());

        when(cardRepository.findCardByBrand(CardBrand.VISA)).thenReturn(detailsCards);

        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String detailsCardsListJson = objectMapper.writeValueAsString(detailsCards);
        detailsCardsListJson = detailsCardsListJson.replace("[2025,10,24]", "\"2025-10-24\"");

        mvc.perform(get("/cards/card?brand={brand}", CardBrand.VISA)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(detailsCardsListJson));
    }

    @Test
    @WithMockUser
    @DisplayName("It should return 404 status if the requested brand is not found.")
    public void testListOfCardsByBrand_Failure() throws Exception {
        when(cardRepository.findCardByBrand(CardBrand.AMEX)).thenReturn(Collections.emptyList());
        var response = mvc.perform(get("/cards/card?brand={brand}", CardBrand.AMEX)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @WithMockUser
    @DisplayName("It should return status 200 if the card details are found.")
    public void testDetailsCard_Success() throws Exception {
        userCreated.setId(1L);

        Card cardCreated = new Card(new RegisterCardDTO(
                "123456789",
                CardBrand.VISA,
                "123",
                1L
        ));

        cardCreated.setId(1L);
        cardCreated.setUser(userCreated);

        when(cardRepository.existsById(cardCreated.getId())).thenReturn(true);
        when(cardRepository.getReferenceById(cardCreated.getId())).thenReturn(cardCreated);

        URI uri = UriComponentsBuilder.fromPath("/cards/{id}")
                .buildAndExpand(cardCreated.getId())
                .toUri();

        mvc.perform(get(uri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("It should return status 404 if the card details are not found.")
    public void testDetailsCard_Failure() throws Exception {
        userCreated.setId(1L);

        Card cardCreated = new Card(new RegisterCardDTO(
                "123456789",
                CardBrand.VISA,
                "123",
                1L
        ));
        cardCreated.setId(1L);
        cardCreated.setUser(userCreated);

        when(cardRepository.existsById(cardCreated.getId())).thenReturn(false);

        URI uri = UriComponentsBuilder.fromPath("/cards/{id}")
                .buildAndExpand(cardCreated.getId())
                .toUri();

        mvc.perform(get(uri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("It should return 201 status when the request is made successfully.")
    public void testRegisterCard_Success() throws Exception {
        var cardData = new RegisterCardDTO("5443 3840 8887 8311", CardBrand.VISA, "123", 1L);
        var user = new User();
        user.setId(1L);

        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(cardRepository.save(Mockito.any(Card.class))).thenReturn(new Card(cardData));

        String cardOfUserJSON = new ObjectMapper().writeValueAsString(cardData);

        mvc.perform(post("/cards/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cardOfUserJSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    @DisplayName("It should return status 404 and no registered user data in case of failure")
    public void testRegisterCard_Failure() throws Exception {
        when(cardRepository.save(any(Card.class))).thenThrow(new DataIntegrityViolationException("Error entering user information."));

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/cards/create");

        mvc.perform(post("/cards/create")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("It should return status 200 and user data with updated information")
    public void testUpdateCard_Success() throws Exception {
        userCreated.setId(1L);

        var updateData = new UpdateCardDTO(
                "9874 6754 3421 2323",
                CardBrand.MASTERCARD,
                LocalDate.of(2025, 10, 24),
                "456"
        );

        var existingCard = new Card(new RegisterCardDTO(
                "123456789",
                CardBrand.VISA,
                "123",
                1L
        ));

        existingCard.setId(1L);
        existingCard.setUser(userCreated);

        when(cardRepository.getReferenceById(1L)).thenReturn(existingCard);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String json = objectMapper.writeValueAsString(updateData);
        mvc.perform(MockMvcRequestBuilders.patch("/cards/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isAccepted());
    }

    @Test
    @WithMockUser
    @DisplayName("It should return status 400 (Bad Request) and no user data with updated information in case of failure")
    public void testUpdateCard_Failure() throws Exception {
        userCreated.setId(1L);

        var updateData = new UpdateCardDTO(
                "3421 2323",
                CardBrand.MASTERCARD,
                LocalDate.of(2019, 10, 24),
                "12"
        );

        var existingCard = new Card(new RegisterCardDTO(
                "123456789",
                CardBrand.VISA,
                "123",
                1L
        ));

        existingCard.setId(1L);
        existingCard.setUser(userCreated);

        when(cardRepository.getReferenceById(1L)).thenReturn(existingCard);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String json = objectMapper.writeValueAsString(updateData);
        mvc.perform(MockMvcRequestBuilders.patch("/cards/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Test
    @WithMockUser
    @DisplayName("It should return status 204 when the card with the given ID exists in the database and is successfully deleted.")
    public void testDeleteExistingCard() throws Exception {
        long validID = 1L;

        when(cardRepository.existsById(validID)).thenReturn(true);

        var response = mvc.perform(delete("/cards/{id}", validID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @WithMockUser
    @DisplayName("It should return status 404 when the card with the given ID does not exist in the database.")
    public void testDeleteNotExistingCard() throws Exception {
        long invalidID = 999L;

        when(cardRepository.existsById(invalidID)).thenReturn(false);

        var response = mvc.perform(delete("/cards/{id}", invalidID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}


