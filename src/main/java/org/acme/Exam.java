package org.acme;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

@Entity
@Table(name = "exams")
public class Exam extends PanacheEntity {

    @NotBlank(message = "O nome do exame não pode ser vazio")
    public String name;

    public int examYear;

    @ManyToMany(mappedBy = "exams", fetch = FetchType.LAZY)
    @JsonIgnore
    public Set<Hunter> participants;
}