package org.dev.filerouge.domain;

import lombok.*;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "students")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("STUDENT")
@Getter
@Setter
@NoArgsConstructor
public class Student extends User {
    @ManyToOne
    private Class studentClass;

    @OneToOne
    private Parent parent;

    @OneToMany(mappedBy = "student")
    private Set<Absence> absences;

    @OneToMany(mappedBy = "student")
    private Set<Grade> grades;
}
