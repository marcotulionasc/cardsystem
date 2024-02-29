package br.com.hst.cardsystem.domain.user;

import br.com.hst.cardsystem.domain.address.UF;
import br.com.hst.cardsystem.domain.address.dto.AddressDTO;
import br.com.hst.cardsystem.domain.user.dto.RegisterUserDTO;
import br.com.hst.cardsystem.domain.user.dto.UpdateUserDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        RegisterUserDTO registerUserDTO = new RegisterUserDTO("Marco Nascimento","14932609604","19997602293",CivilStatus.MARRIED,"01/01/2000","Engineer",new BigDecimal(50000),new AddressDTO("Rua 123", "203", "Sacramento", "13100350", UF.SP, "Casa"));
        user = new User(registerUserDTO);
    }

    @Test
    void testCreateUserFromRegisterUserDTO() {
        Assertions.assertEquals("Marco Nascimento", user.getName());
        Assertions.assertEquals("14932609604", user.getCpf());
        Assertions.assertEquals("19997602293", user.getContactNumber());
        Assertions.assertEquals(CivilStatus.MARRIED, user.getCivilStatus());
        Assertions.assertEquals("01/01/2000", user.getBirthdate());
        Assertions.assertEquals("Engineer", user.getOccupation());
        Assertions.assertEquals(BigDecimal.valueOf(50000), user.getSalary());
        Assertions.assertTrue(user.isCurrentlyActive());
        Assertions.assertNotNull(user.getAddress());
    }
    @Test
    void testUpdateUserData() {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO("Marco Nascimento", "19997602293",CivilStatus.MARRIED,"01/01/2000","Engineer", new BigDecimal(50000), new AddressDTO("Rua 123", "203", "Sacramento", "13100350", UF.SP, "Casa"));
        user.updateUserData(updateUserDTO);

        Assertions.assertEquals("Marco Nascimento", user.getName());
        Assertions.assertEquals("19997602293", user.getContactNumber());
        Assertions.assertEquals(CivilStatus.MARRIED, user.getCivilStatus());
        Assertions.assertEquals("01/01/2000", user.getBirthdate());
        Assertions.assertEquals("Engineer", user.getOccupation());
        Assertions.assertEquals(BigDecimal.valueOf(50000), user.getSalary());
    }
    @Test
    void testInactiveUser() {
        user.inactive();
        Assertions.assertFalse(user.isCurrentlyActive());
    }

    @Test
    void testActiveUser() {
        Assertions.assertTrue(user.isCurrentlyActive());
    }
}
