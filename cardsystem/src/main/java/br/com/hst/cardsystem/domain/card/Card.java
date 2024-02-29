/*
 * (c) COPYRIGHT 2023 HST EQUIPAMENTOS
 * ELETRONICOS Ltda, Campinas (SP), Brasil
 * ALL RIGHTS RESERVED - TODOS OS DIREITOS RESERVADOS
 * CONFIDENTIAL, UNPUBLISHED PROPERTY OF HST E. E. Ltda
 * PROPRIEDADE CONFIDENCIAL NAO PUBLICADA DA HST Ltda.
 */
package br.com.hst.cardsystem.domain.card;

import br.com.hst.cardsystem.domain.card.dto.RegisterCardDTO;
import br.com.hst.cardsystem.domain.card.dto.UpdateCardDTO;
import br.com.hst.cardsystem.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Base64;

@Entity(name = "Card")
@Table(name = "cards")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numberCard;

    @Enumerated(EnumType.STRING)
    private CardBrand cardBrand;

    private LocalDate dateExpiration;

    private String cvv;

    @Column(unique = true)
    private String originalNumberCard;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Card(RegisterCardDTO data) {
        this.numberCard = data.numberCard();
        this.cardBrand = data.cardBrand();
        this.dateExpiration = LocalDate.now().plusYears(2);
        this.cvv = data.cvv();
    }

    public void updateCardData(UpdateCardDTO data) {
        this.numberCard = (data.numberCard() != null) ? data.numberCard() : this.numberCard;
        this.cardBrand = (data.cardBrand() != null) ? data.cardBrand() : this.cardBrand;
        this.dateExpiration = (data.dateExpiration() != null) ? data.dateExpiration() : this.dateExpiration;
        this.cvv = (data.cvv() != null) ? data.cvv() : this.cvv;
    }

    public void maskCardNumber() {

        if (this.numberCard != null && this.numberCard.length() >= 4) {

            String masked = this.numberCard.substring(0, this.numberCard.length() - 4).replaceAll("\\d", "*");
            masked += this.numberCard.substring(this.numberCard.length() - 4);

            this.originalNumberCard = encodeToBase64(this.numberCard);
            this.numberCard = masked;

        }
    }

    private String encodeToBase64(String input) {
        try {
            byte[] data = input.getBytes("UTF-8");
            byte[] encoded = Base64.getEncoder().encode(data);
            return new String(encoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
