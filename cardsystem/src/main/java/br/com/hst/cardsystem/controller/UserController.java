/*
 * (c) COPYRIGHT 2023 HST EQUIPAMENTOS
 * ELETRONICOS Ltda, Campinas (SP), Brasil
 * ALL RIGHTS RESERVED - TODOS OS DIREITOS RESERVADOS
 * CONFIDENTIAL, UNPUBLISHED PROPERTY OF HST E. E. Ltda
 * PROPRIEDADE CONFIDENCIAL NAO PUBLICADA DA HST Ltda.
 */
package br.com.hst.cardsystem.controller;

import br.com.hst.cardsystem.domain.card.CardRepository;
import br.com.hst.cardsystem.domain.card.dto.CardOfUserDTO;
import br.com.hst.cardsystem.domain.user.User;
import br.com.hst.cardsystem.domain.user.UserRepository;
import br.com.hst.cardsystem.domain.user.dto.*;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("users")
@Tag(name = "User", description = "Responsible for requests related to users.")
@SecurityRequirement(name = "bearer-key")
public class UserController {

    private List<User> users;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    @Operation(
            summary = "Search user by status",
            operationId = "userByStatus",
            description = "This request search a user by activity status."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Users were found, returns the list of users with requested status.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404", description = "There are no users with the requested status.",
                    content = @Content(mediaType = "")
            ),
    })
    @GetMapping("/status")
    public ResponseEntity<List<UserByStatusDTO>> userByStatus(@RequestParam(value = "active") boolean currentlyActive) {
        users = userRepository.findByCurrentlyActive(currentlyActive);

        if (users.isEmpty()) {
            return new ResponseEntity("There are no users with the defined status.", HttpStatus.NOT_FOUND);
        }

        var listUserStatus = users.stream()
                .map(user -> new UserByStatusDTO(user.getId(), user.getName(), user.getCpf()))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(listUserStatus);
    }

    @Operation(
            summary = "Search user by salary",
            operationId = "userBySalary",
            description = "This request search a user by salary range."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Users were found, returns the list of users with requested salary range.",
                    content = @Content(mediaType = "")
            ),
            @ApiResponse(
                    responseCode = "404", description = "There are no users with the requested salary range.",
                    content = @Content(mediaType = "")
            ),
    })
    @GetMapping("/salary")
    public ResponseEntity<List<UserBySalaryDTO>> userBySalary(@RequestParam(value = "min") BigDecimal salaryMin, @RequestParam(value = "max") BigDecimal salaryMax) {
        users = userRepository.findBySalary(salaryMin, salaryMax);

        List<UserBySalaryDTO> userBySalaryDTOS = users.stream()
                .map(user -> new UserBySalaryDTO(user.getName(), user.getCpf(), user.getOccupation(), user.getSalary()))
                .toList();

        if (userBySalaryDTOS.isEmpty()) {
            return new ResponseEntity("Doesn't exists active users with salary range.", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.status(HttpStatus.OK).body(userBySalaryDTOS);
    }

    @Operation(
            summary = "Search for cards using user ID",
            operationId = "cardsOfUser",
            description = "This request searches for user cards using the user id."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Cards were found, returns the list of cards according to the requested user id.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404", description = "Doesn't exist cards for this id.",
                    content = @Content(mediaType = "")
            ),
    })
    @GetMapping("/cards/{id}")
    public ResponseEntity<List<CardOfUserDTO>> cardsOfUser(@PathVariable Long id) {

        if (!userRepository.existsById(id)) {
            return new ResponseEntity("This id doesn't exists in the database.", HttpStatus.NOT_FOUND);
        }

        var cards = cardRepository.findByUserId(id);
        var cardDTOs = cards.stream()
                .map(card -> new CardOfUserDTO(id, card.getId(), card.getNumberCard(), card.getCardBrand(),
                        card.getDateExpiration(), card.getCvv()))
                .collect(Collectors.toList());

        if (cardDTOs.isEmpty()) {
            return new ResponseEntity("There are no cards for the requested user ID.", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.status(HttpStatus.OK).body(cardDTOs);
    }

    @Operation(
            summary = "Details the user by id",
            operationId = "detailsUser",
            description = "This request details a user by id."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "User found, returns the data of the user searched for by id.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404", description = "No user with this id was found.",
                    content = @Content(mediaType = "")
            ),
    })
    @GetMapping("/{id}")
    public ResponseEntity<DetailsUserDTO> detailsUser(
            @Parameter(description = "The ID of the user to be detailed", required = true) @PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return new ResponseEntity("Doesn't exists user with this id", HttpStatus.NOT_FOUND);
        }

        var user = userRepository.getReferenceById(id);
        return ResponseEntity.status(HttpStatus.OK).body(new DetailsUserDTO(user));
    }

    @Operation(
            summary = "Register new user",
            operationId = "registerUser",
            description = "This request registers a new user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "User registered successfully, returns data user.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400", description = "Information entered incorrectly.",
                    content = @Content(mediaType = "")
            ),
    })
    @PostMapping
    @Transactional
    public ResponseEntity<DetailsUserDTO> registerUser(@RequestBody @Valid RegisterUserDTO data, UriComponentsBuilder uriBuilder) {
        var user = new User(data);
        userRepository.save(user);

        var uri = uriBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(new DetailsUserDTO(user));
    }

    @Operation(
            summary = "Update the user by id",
            operationId = "updateUser",
            description = "This request update a user by id."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202", description = "User information has been updated, returns user data.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404", description = "No user with this id was found.",
                    content = @Content(mediaType = "")
            ),
    })
    @PatchMapping("/{id}")
    @Transactional
    public ResponseEntity<DetailsUserDTO> updateUser(
            @Parameter(description = "The ID of the user to be updated", required = true) @PathVariable Long id, @RequestBody @Valid UpdateUserDTO data) {
        if (!userRepository.existsById(id)) {
            return new ResponseEntity("Doesn't exists user with this id.", HttpStatus.NOT_FOUND);
        }

        var user = userRepository.getReferenceById(id);
        user.updateUserData(data);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new DetailsUserDTO((user)));
    }

    @Operation(
            summary = "Inactive the user by id",
            operationId = "inactiveUser",
            description = "This request inactive a user by id."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", description = "User successfully inactivated.",
                    content = @Content(mediaType = "")
            ),
            @ApiResponse(
                    responseCode = "404", description = "No user with this id was found.",
                    content = @Content(mediaType = "")
            ),
    })
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<User> inactiveUser(
            @Parameter(description = "The ID of the user to be inactivated", required = true) @PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return new ResponseEntity("Doesn't exists user with this id", HttpStatus.NOT_FOUND);
        }

        var user = userRepository.getReferenceById(id);
        user.inactive();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}