package org.dev.filerouge.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(mappedBy = "program")
    @JsonManagedReference("program-class")
    private List<Class> classes = new ArrayList<>();

    @OneToMany(mappedBy = "program")
    @JsonManagedReference("program-subject")
    private List<Subject> subjects = new ArrayList<>();

    private String description;
}
