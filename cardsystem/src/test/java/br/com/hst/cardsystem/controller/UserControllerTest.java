/*
 * (c) COPYRIGHT 2023 HST EQUIPAMENTOS
 * ELETRONICOS Ltda, Campinas (SP), Brasil
 * ALL RIGHTS RESERVED - TODOS OS DIREITOS RESERVADOS
 * CONFIDENTIAL, UNPUBLISHED PROPERTY OF HST E. E. Ltda
 * PROPRIEDADE CONFIDENCIAL NAO PUBLICADA DA HST Ltda.
 */
package br.com.hst.cardsystem.controller;

import br.com.hst.cardsystem.domain.address.Address;
import br.com.hst.cardsystem.domain.address.UF;
import br.com.hst.cardsystem.domain.address.dto.AddressDTO;
import br.com.hst.cardsystem.domain.card.Card;
import br.com.hst.cardsystem.domain.card.CardBrand;
import br.com.hst.cardsystem.domain.card.CardRepository;
import br.com.hst.cardsystem.domain.card.dto.CardOfUserDTO;
import br.com.hst.cardsystem.domain.card.dto.RegisterCardDTO;
import br.com.hst.cardsystem.domain.user.CivilStatus;
import br.com.hst.cardsystem.domain.user.User;
import br.com.hst.cardsystem.domain.user.UserRepository;
import br.com.hst.cardsystem.domain.user.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private UserController userController;

    private User createUser(String name, String cpf, String contactNumber, CivilStatus civilStatus, String birthdate,
                            String occupation, BigDecimal salary, AddressDTO addressDTO) {
        User user = new User();
        user.setName(name);
        user.setCpf(cpf);
        user.setContactNumber(contactNumber);
        user.setCivilStatus(civilStatus);
        user.setBirthdate(birthdate);
        user.setOccupation(occupation);
        user.setSalary(salary);
        user.setAddress(new Address(addressDTO));
        user.setId(null);

        return user;
    }

    private final AddressDTO userAddressDTO = new AddressDTO(
            "Rua Jose Moises de Nazar",
            "209",
            "Jardim São Fernando",
            "13100350",
            UF.SP,
            "Casa"
    );

    @Test
    @WithMockUser
    @DisplayName("It should return 200 status and list users data with the requested status")
    public void testUserSearchByStatus_Success() throws Exception {
        List<User> listUsers = new ArrayList<>();
        listUsers.add(createUser("User 1", "1234567890", "19988776655", CivilStatus.SINGLE,
                "01/01/1990", "Software Developer", BigDecimal.valueOf(5000), userAddressDTO));
        listUsers.add(createUser("User 2", "0987654321", "19988661918", CivilStatus.MARRIED,
                "02/02/1995", "Designer", BigDecimal.valueOf(6000), userAddressDTO));

        when(userRepository.findByCurrentlyActive(true)).thenReturn(listUsers);

        ResponseEntity<List<UserByStatusDTO>> responseEntity = userController.userByStatus(true);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        List<UserByStatusDTO> userByStatusDTOList = responseEntity.getBody();
        assertEquals(listUsers.size(), userByStatusDTOList.size());
    }

    @Test
    @WithMockUser
    @DisplayName("It should return 404 status and message when user list is empty")
    public void testUserSearchByStatus_Failure() throws Exception {
        List<User> listUsers = new ArrayList<>();
        listUsers.add(createUser("User 1", "1234567890", "19988776655", CivilStatus.SINGLE,
                "01/01/1990", "Software Developer", BigDecimal.valueOf(5000), userAddressDTO));
        listUsers.add(createUser("User 2", "0987654321", "19988661918", CivilStatus.MARRIED,
                "02/02/1995", "Designer", BigDecimal.valueOf(6000), userAddressDTO));

        when(userRepository.findByCurrentlyActive(true)).thenReturn(Collections.emptyList());

        ResponseEntity<List<UserByStatusDTO>> responseEntity = userController.userByStatus(true);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("There are no users with the defined status.", responseEntity.getBody());
    }

    @Test
    @WithMockUser
    @DisplayName("It should return 200 status and users with the requested salary range")
    public void testUserBySalary_Success() throws Exception {
        List<User> listUsers = new ArrayList<>();
        var userWithSalaryRange = createUser("User 1", "1234567890", "19988776655", CivilStatus.SINGLE,
                "01/01/1990", "Software Developer", BigDecimal.valueOf(4000), userAddressDTO);
        var userOutsideSalaryRange = createUser("User 2", "0987654321", "19988661918", CivilStatus.MARRIED,
                "02/02/1995", "Designer", BigDecimal.valueOf(6000), userAddressDTO);

        listUsers.add(userWithSalaryRange);
        listUsers.add(userOutsideSalaryRange);

        List<User> usersWithinSalaryRangeList = Collections.singletonList(userWithSalaryRange);

        when(userRepository.findBySalary(BigDecimal.valueOf(3000), BigDecimal.valueOf(5500)))
                .thenReturn(usersWithinSalaryRangeList);

        ResponseEntity<List<UserBySalaryDTO>> responseEntity = userController.userBySalary(BigDecimal.valueOf(3000), BigDecimal.valueOf(5500));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        List<UserBySalaryDTO> userByStatusDTOList = responseEntity.getBody();
        assertEquals(usersWithinSalaryRangeList.size(), userByStatusDTOList.size());
    }

    @Test
    @WithMockUser
    @DisplayName("It should return 404 status and message when user list is empty")
    public void testUserBySalary_Failure() throws Exception {
        List<User> listUsers = new ArrayList<>();
        listUsers.add(createUser("User 1", "1234567890", "19988776655", CivilStatus.SINGLE,
                "01/01/1990", "Software Developer", BigDecimal.valueOf(5000), userAddressDTO));
        listUsers.add(createUser("User 2", "0987654321", "19988661918", CivilStatus.MARRIED,
                "02/02/1995", "Designer", BigDecimal.valueOf(6000), userAddressDTO));

        when(userRepository.findBySalary(BigDecimal.valueOf(7000), BigDecimal.valueOf(10000)))
                .thenReturn(listUsers);

        ResponseEntity<List<UserBySalaryDTO>> responseEntity = userController.userBySalary(BigDecimal.valueOf(3000), BigDecimal.valueOf(5500));

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Doesn't exists active users with salary range.", responseEntity.getBody());
    }

    @Test
    @WithMockUser
    @DisplayName("It should return status 200 and the list of cards according to the user id")
    public void testCardsOfUser_Success() {
        var userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        List<Card> listCards = List.of(
                new Card(new RegisterCardDTO("12345678910", CardBrand.VISA, "123", userId)),
                new Card(new RegisterCardDTO("52436472819", CardBrand.MASTERCARD, "321", userId))
        );

        when(cardRepository.findByUserId(userId)).thenReturn(listCards);

        ResponseEntity<List<CardOfUserDTO>> responseEntity = userController.cardsOfUser(userId);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        List<CardOfUserDTO> cardDTOs = responseEntity.getBody();
        assertEquals(listCards.size(), cardDTOs.size());

        for (int i = 0; i < listCards.size(); i++) {
            var mockCard = listCards.get(i);
            var cardDTO = cardDTOs.get(i);

            assertEquals(userId, cardDTO.idUser());
            assertEquals(mockCard.getId(), cardDTO.id());
            assertEquals(mockCard.getNumberCard(), cardDTO.numberCard());
            assertEquals(mockCard.getCardBrand(), cardDTO.cardBrand());
            assertEquals(mockCard.getCvv(), cardDTO.cvv());
        }
    }

    @Test
    @WithMockUser
    @DisplayName("It should return status 404 and message when the id entered does not exist")
    public void testCardsOfUser_Failure() throws Exception {
        var userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        ResponseEntity<List<CardOfUserDTO>> responseEntity = userController.cardsOfUser(userId);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("This id doesn't exists in the database.", responseEntity.getBody());
    }

    @Test
    @WithMockUser
    @DisplayName("It should return status 200 and user data with the requested id")
    public void testDetailsUser_Success() throws Exception {
        var userCreated = createUser(
                "João da Silva",
                "1234567890",
                "19988776655",
                CivilStatus.SINGLE,
                "01/01/1990",
                "Software Developer",
                BigDecimal.valueOf(4000),
                userAddressDTO
        );
        userCreated.setId(1L);

        when(userRepository.existsById(userCreated.getId())).thenReturn(true);
        when(userRepository.getReferenceById(userCreated.getId())).thenReturn(userCreated);

        ResponseEntity<DetailsUserDTO> responseEntity = userController.detailsUser(userCreated.getId());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        var detailsUserDTO = responseEntity.getBody();

        assertEquals(userCreated.getId(), detailsUserDTO.id());
        assertEquals(userCreated.getCpf(), detailsUserDTO.cpf());
        assertEquals(userCreated.getContactNumber(), detailsUserDTO.contactNumber());
        assertEquals(userCreated.getCivilStatus(), detailsUserDTO.civilStatus());
        assertEquals(userCreated.getBirthdate(), detailsUserDTO.birthdate());
        assertEquals(userCreated.getOccupation(), detailsUserDTO.occupation());
        assertEquals(userCreated.getSalary(), detailsUserDTO.salary());
        assertEquals(userCreated.getAddress(), detailsUserDTO.address());
    }

    @Test
    @WithMockUser
    @DisplayName("It should return status 404 when user doesn't exist in database")
    public void testDetailsUser_Failure() throws Exception {
        var userId = 1L;
        when(userRepository.getReferenceById(userId)).thenReturn(null);

        ResponseEntity<DetailsUserDTO> responseEntity = userController.detailsUser(userId);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    @WithMockUser
    @DisplayName("It should return status 200 and the registered user data")
    public void testRegisterUser_Success() throws Exception {
        var uriBuilder = mock(UriComponentsBuilder.class);

        var testData = new RegisterUserDTO(
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
        );

        when(userRepository.save(any(User.class))).thenReturn(new User(testData));
        when(uriBuilder.path(anyString())).thenReturn(UriComponentsBuilder.fromPath("/users/1"));

        ResponseEntity<DetailsUserDTO> responseEntity = userController.registerUser(testData, uriBuilder);
        var userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        assertEquals(201, responseEntity.getStatusCodeValue());
        assertEquals("/users/1", responseEntity.getHeaders().getLocation().getPath());

        var savedUser = userCaptor.getValue();
        assertEquals("João da Silva", savedUser.getName());
        assertEquals("12345678901", savedUser.getCpf());
        assertEquals("123-456-7890", savedUser.getContactNumber());
        assertEquals("MARRIED", savedUser.getCivilStatus().toString());
        assertEquals("01/01/1990", savedUser.getBirthdate());
        assertEquals("Engineer", savedUser.getOccupation());
        assertEquals(BigDecimal.valueOf(60000), savedUser.getSalary());
    }

    @Test
    @WithMockUser
    @DisplayName("It should return status 400 when some information is entered incorrectly")
    public void testRegisterUser_Failure() throws Exception {
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("Error entering user information."));

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/users/{id}");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("It should return status 200 and user data with updated information")
    public void testUpdateUser_Success() throws Exception {
        var userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        var updateUserData = new UpdateUserDTO(
                "João Mário da Silva",
                "(19) 98866-1839",
                CivilStatus.MARRIED,
                "01/01/1990",
                "Software Developer SR",
                BigDecimal.valueOf(5500),
                userAddressDTO
        );

        var user = createUser(
                "João da Silva",
                "1234567890",
                "(19) 98866-1339",
                CivilStatus.SINGLE,
                "01/01/1990",
                "Software Developer",
                BigDecimal.valueOf(4000),
                userAddressDTO
        );
        user.setId(userId);
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        ResponseEntity<DetailsUserDTO> response = userController.updateUser(userId, updateUserData);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        verify(userRepository).getReferenceById(userId);

        assertEquals("João Mário da Silva", user.getName());
        assertEquals("(19) 98866-1839", user.getContactNumber());
        assertEquals(CivilStatus.MARRIED, user.getCivilStatus());
        assertEquals("Software Developer SR", user.getOccupation());
        assertEquals(BigDecimal.valueOf(5500), user.getSalary());
    }

    @Test
    @WithMockUser
    @DisplayName("It should return 404 status if the user id is not in the database")
    public void testUpdateUser_Failure() throws Exception {
        var userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        var updateUserData = new UpdateUserDTO(
                "João Mário da Silva",
                "(19) 98866-1839",
                CivilStatus.MARRIED,
                "01/01/1990",
                "Software Developer SR",
                BigDecimal.valueOf(5500),
                userAddressDTO
        );

        var user = createUser(
                "João da Silva",
                "1234567890",
                "(19) 98866-1339",
                CivilStatus.SINGLE,
                "01/01/1990",
                "Software Developer",
                BigDecimal.valueOf(4000),
                userAddressDTO
        );
        user.setId(userId);

        ResponseEntity<DetailsUserDTO> response = userController.updateUser(userId, updateUserData);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, never()).getReferenceById(userId);
    }

    @Test
    @WithMockUser
    @DisplayName("It should return 204 status when the id entered is valid")
    public void testInactiveUser_Success() throws Exception {
        var userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);

        var response = mockMvc.perform(delete("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @WithMockUser
    @DisplayName("It should return 404 status when the id entered is invalid")
    public void testInactiveUser_Failure() throws Exception {
        var userInvalidId = 999L;

        when(userRepository.existsById(userInvalidId)).thenReturn(false);

        var response = mockMvc.perform(delete("/users/{id}", userInvalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}