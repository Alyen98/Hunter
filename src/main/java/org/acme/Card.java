package org.acme;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "cards")
public class Card extends PanacheEntity {

    @NotBlank(message="A habilidade Nen não pode ser vazia")
    public String nenAbility;

    public String exam;

    @NotNull(message="O tipo de Nen é obrigatório")
    public NenType nenType;

    @ManyToOne
    @NotNull(message="O card deve pertencer a um hunter")
    @JsonBackReference
    public Hunter hunter;

    public Card() {
    }
}