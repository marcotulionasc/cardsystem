/*
 * (c) COPYRIGHT 2023 HST EQUIPAMENTOS
 * ELETRONICOS Ltda, Campinas (SP), Brasil
 * ALL RIGHTS RESERVED - TODOS OS DIREITOS RESERVADOS
 * CONFIDENTIAL, UNPUBLISHED PROPERTY OF HST E. E. Ltda
 * PROPRIEDADE CONFIDENCIAL NAO PUBLICADA DA HST Ltda.
 */
package br.com.hst.cardsystem.domain.user;

import br.com.hst.cardsystem.domain.address.Address;
import br.com.hst.cardsystem.domain.user.dto.RegisterUserDTO;
import br.com.hst.cardsystem.domain.user.dto.UpdateUserDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;

@Entity(name="User")
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @CPF
    @Column(unique = true)
    private String cpf;

    private String contactNumber;

    @Enumerated(EnumType.STRING)
    private CivilStatus civilStatus;

    @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/(19\\d\\d|20[01]\\d|202[0-2])$")
    private String birthdate;

    private String occupation;

    private BigDecimal salary;

    private boolean currentlyActive;

    @Embedded
    private Address address;

    public User (RegisterUserDTO data) {
        this.currentlyActive = true;
        this.name = data.name();
        this.cpf = data.cpf();
        this.contactNumber = data.contactNumber();
        this.civilStatus = data.civilStatus();
        this.birthdate = data.birthdate();
        this.occupation = data.occupation();
        this.salary = data.salary();
        this.address = new Address(data.address());
    }

    public void updateUserData(UpdateUserDTO data) {
        this.name = (data.name() != null) ? data.name() : this.name;
        this.contactNumber = (data.contactNumber() != null) ? data.contactNumber() : this.contactNumber;
        this.civilStatus = (data.civilStatus() != null) ? data.civilStatus() : this.civilStatus;
        this.birthdate = (data.birthdate() != null) ? data.birthdate() : this.birthdate;
        this.occupation = (data.occupation() != null) ? data.occupation() : this.occupation;
        this.salary = (data.salary() != null) ? data.salary() : this.salary;

        if (data.address() != null) {
            address.updateAddressData(data.address());
        }
    }

    public void inactive(){
        this.currentlyActive = false;
    }

}
