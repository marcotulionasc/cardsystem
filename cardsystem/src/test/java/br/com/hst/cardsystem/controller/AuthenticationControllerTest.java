/*
 * (c) COPYRIGHT 2023 HST EQUIPAMENTOS
 * ELETRONICOS Ltda, Campinas (SP), Brasil
 * ALL RIGHTS RESERVED - TODOS OS DIREITOS RESERVADOS
 * CONFIDENTIAL, UNPUBLISHED PROPERTY OF HST E. E. Ltda
 * PROPRIEDADE CONFIDENCIAL NAO PUBLICADA DA HST Ltda.
 */
package br.com.hst.cardsystem.controller;

import br.com.hst.cardsystem.domain.userlogin.UserLogin;
import br.com.hst.cardsystem.domain.userlogin.UserLoginRepository;
import br.com.hst.cardsystem.domain.userlogin.dto.UserLoginDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserLoginRepository userRepository;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    @DisplayName("It should return 202 status when login is successful")
    public void testLogin_Success() throws Exception {
        String login = "hst@hst.com.br";
        String password = "password123";
        UserLogin userLogin = new UserLogin(login, new BCryptPasswordEncoder().encode(password));

        when(userRepository.findByLogin(login)).thenReturn(userLogin);

        UserLoginDTO userLoginDTO = new UserLoginDTO(login, password);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userLoginDTO)))
                .andExpect(status().isAccepted());
    }

    @Test
    @DisplayName("It should return 400 status when the login is incorrect or invalid")
    public void testLogin_Failure() throws Exception {
        String login = "example456";
        String password = "password123";

        when(userRepository.findByLogin(login)).thenReturn(null);

        UserLoginDTO userLoginDTO = new UserLoginDTO(login, password);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userLoginDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("It should return 201 status when user registration is successful")
    public void testRegisterUser_Success() throws Exception {
        UserLoginDTO validUserDTO = new UserLoginDTO("hst@hst.com.br", "1234567");

        when(userRepository.findByLogin(validUserDTO.login())).thenReturn(null);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/auth/{id}");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validUserDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("It should return 400 status when the user record already exists in the system")
    public void testRegisterUser_Failure() throws Exception {
        UserLoginDTO validUserDTO = new UserLoginDTO("hst@hst.com.br", "1234567");

        when(userRepository.findByLogin(validUserDTO.login())).thenReturn(new UserLogin(validUserDTO.login(), "1234567"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validUserDTO)))
                .andExpect(status().isBadRequest());
    }
}
