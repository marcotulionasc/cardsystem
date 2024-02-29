/*
 * (c) COPYRIGHT 2023 HST EQUIPAMENTOS
 * ELETRONICOS Ltda, Campinas (SP), Brasil
 * ALL RIGHTS RESERVED - TODOS OS DIREITOS RESERVADOS
 * CONFIDENTIAL, UNPUBLISHED PROPERTY OF HST E. E. Ltda
 * PROPRIEDADE CONFIDENCIAL NAO PUBLICADA DA HST Ltda.
 */
package br.com.hst.cardsystem.controller;

import br.com.hst.cardsystem.domain.card.Card;
import br.com.hst.cardsystem.domain.card.CardBrand;
import br.com.hst.cardsystem.domain.card.CardRepository;
import br.com.hst.cardsystem.domain.card.dto.*;
import br.com.hst.cardsystem.domain.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Base64;

@RestController
@RequestMapping("cards")
@Tag(name = "Card", description = "Responsible for requests related to cards.")
@SecurityRequirement(name = "bearer-key")
public class CardController {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;

    @Operation(
            summary = "List cards by brand",
            operationId = "listOfCardsByBrand",
            description = "This request list cards by brand."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Cards were found, returns list of cards by brand.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404", description = "Doesn't exist cards for this brand.",
                    content = @Content(mediaType = "")
            ),
    })
    @GetMapping("/card")
    public ResponseEntity listOfCardsByBrand(@RequestParam(value = "brand") CardBrand cardBrand) {
        var cards = cardRepository.findCardByBrand(cardBrand);

        if (cards.isEmpty()) {
            return new ResponseEntity("Doesn't exist cards for this brand.", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.status(HttpStatus.OK).body(cards);
    }

    @Operation(
            summary = "Details the card by id",
            operationId = "detailsCard",
            description = "This request details a card by id."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "The card was found, returns the card data.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404", description = "Doesn't exists a data with this ID number.",
                    content = @Content(mediaType = "")
            ),
    })
    @GetMapping("/{id}")
    public ResponseEntity<DetailsCardEncryptedDTO> detailsCard(@PathVariable Long id) {
        if (!cardRepository.existsById(id)) {
            return new ResponseEntity("This id doesn't exists in the database.", HttpStatus.NOT_FOUND);
        }

        var card = cardRepository.getReferenceById(id);
        var dto = new DetailsCardEncryptedDTO(card);

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @Operation(
            summary = "Register new card",
            operationId = "registerCard",
            description = "This request register a new card."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "Card has been created, returns encrypted card data.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400", description = "This information was not found.",
                    content = @Content(mediaType = "")
            ),
    })
    @PostMapping("/create")
    @Transactional
    public ResponseEntity registerCard(@RequestBody @Valid RegisterCardDTO data, UriComponentsBuilder uriBuilder) {
        var user = userRepository.getReferenceById(data.userId());
        var card = new Card(data);
        var cvv = data.cvv();

        if (!cvv.matches("^[0-9]{3}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("CVV should contains 3 numbers.");
        }

        String encodedCVV = Base64.getEncoder().encodeToString(data.cvv().getBytes());

        card.setCvv(encodedCVV);
        card.maskCardNumber();
        card.setUser(user);

        cardRepository.save(card);
        var uri = uriBuilder.path(("cards/{id}")).buildAndExpand(card.getId()).toUri();
        return ResponseEntity.status(HttpStatus.CREATED).body(new CardOfUserDTO(card));
    }

    @Operation(
            summary = "Update the card by id user",
            operationId = "updateCard",
            description = "This request update a card by id."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202", description = "User information has been updated, returns user data.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404", description = "Doesn't exists a data with this ID number.",
                    content = @Content(mediaType = "")
            ),
    })
    @PatchMapping("/{id}")
    @Transactional
    public ResponseEntity<CardOfUserDTO> updateCard(
            @Parameter(description = "The ID of the card to be updated", required = true) @PathVariable Long id,
            @RequestBody @Valid UpdateCardDTO data) {
        var card = cardRepository.getReferenceById(id);
        card.updateCardData(data);

        var encodedCVV = Base64.getEncoder().encodeToString(data.cvv().getBytes());
        card.setCvv(encodedCVV);
        card.maskCardNumber();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new CardOfUserDTO(card));
    }

    @Operation(
            summary = "Delete the card by id",
            operationId = "deleteCard",
            description = "This request delete a card by id."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", description = "Card successfully deleted.",
                    content = @Content(mediaType = "")
            ),
            @ApiResponse(
                    responseCode = "404", description = "Doesn't exist cards for this id.",
                    content = @Content(mediaType = "")
            ),
    })
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity deleteCard(@Parameter(description = "The ID of the card to be deleted", required = true) @PathVariable Long id) {
        if (cardRepository.existsById(id)) {
            cardRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doesn't exist card for this id.");
        }
    }
}