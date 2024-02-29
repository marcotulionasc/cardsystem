/*
 * (c) COPYRIGHT 2023 HST EQUIPAMENTOS
 * ELETRONICOS Ltda, Campinas (SP), Brasil
 * ALL RIGHTS RESERVED - TODOS OS DIREITOS RESERVADOS
 * CONFIDENTIAL, UNPUBLISHED PROPERTY OF HST E. E. Ltda
 * PROPRIEDADE CONFIDENCIAL NAO PUBLICADA DA HST Ltda.
 */
package br.com.hst.cardsystem.controller;

import br.com.hst.cardsystem.domain.userlogin.dto.UserLoginDTO;
import br.com.hst.cardsystem.domain.userlogin.UserLogin;
import br.com.hst.cardsystem.domain.userlogin.UserLoginRepository;
import br.com.hst.cardsystem.infra.security.DadosTokenJWT;
import br.com.hst.cardsystem.services.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("auth")
@Tag(name = "Authentication", description = "Responsible for authentication-related requests.")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserLoginRepository repository;

    @Autowired
    private TokenService tokenService;

    @Operation(
            summary = "Login to the system",
            operationId = "loginUser",
            description = "This request logs in a registered user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "202", description = "Logged in user, returns the authentication token.",
                    content = @Content(mediaType = "")),
            @ApiResponse(
                    responseCode = "406", description = "Information (login and/or password) entered incorrectly.",
                    content = @Content(mediaType = ""))
    })
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid UserLoginDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var loginRecord = this.repository.findByLogin(data.login());

        if (loginRecord == null || !new BCryptPasswordEncoder().matches(data.password(), loginRecord.getPassword())) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Login or password are incorrect.");
        }

        var auth = this.authenticationManager.authenticate(usernamePassword);
        var tokenJWT = tokenService.createToken((UserLogin) auth.getPrincipal());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new DadosTokenJWT(tokenJWT));
    }

    @Operation(
            summary = "Register new user login",
            operationId = "registerUser",
            description = "This request registers a new login to the system."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "User created with success.",
                    content = @Content(mediaType = "")),
            @ApiResponse(
                    responseCode = "400", description = "Information entered incorrectly or the login already exists in the system.",
                    content = @Content(mediaType = ""))
    })
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid UserLoginDTO data, UriComponentsBuilder uriBuilder) {

        if (this.repository.findByLogin(data.login()) != null) return ResponseEntity.badRequest().build();

        var encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        var NewUserLogin = new UserLogin(data.login(), encryptedPassword);
        this.repository.save(NewUserLogin);

        var uri = uriBuilder.path("/auth/{id}").buildAndExpand(NewUserLogin).toUri();

        return new ResponseEntity("User created with success!", HttpStatus.CREATED);
    }
}