package org.acme;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "hunters")
public class Hunter extends PanacheEntity {

    @NotBlank(message="O nome do hunter não pode ser vazio")
    @Size(min = 3, max = 100, message="O nome deve ter entre 3 e 100 caracteres")
    public String name;

    @Min(value = 1, message = "A idade deve ser um valor positivo")
    public int age;

    @OneToOne(mappedBy = "hunter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public HunterLicense license;

    @OneToMany(mappedBy = "hunter", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    public List<Card> cards;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "hunter_exam",
            joinColumns = @JoinColumn(name = "hunter_id"),
            inverseJoinColumns = @JoinColumn(name = "exam_id"))
    public Set<Exam> exams;
}
