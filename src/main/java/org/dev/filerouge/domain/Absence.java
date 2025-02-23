package org.dev.filerouge.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.dev.filerouge.domain.Enum.AbsenceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private LocalDateTime date;

    @NotNull
    private boolean justification;

    private String remark;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AbsenceStatus status;

    private String justificationText;

    @ManyToOne
    private Student student;
}
