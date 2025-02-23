package org.dev.filerouge.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Min(0)
    @Max(20)
    private Float grade;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Activity activity;
}
