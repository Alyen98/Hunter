package org.acme;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "hunter_licenses")
public class HunterLicense extends PanacheEntity {

    @NotBlank(message = "O número da licença não pode ser vazio")
    public String licenseNumber;

    @NotNull(message = "A data de emissão é obrigatória")
    public LocalDate issueDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    public Hunter hunter;
}
