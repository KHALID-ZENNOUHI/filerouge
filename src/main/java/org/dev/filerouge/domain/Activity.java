package org.dev.filerouge.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.dev.filerouge.domain.Enum.ActivityType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ActivityType type;

    @NotBlank
    private String title;

    @Future
    private LocalDateTime date;

    private String resources;

    private String description;

    @ManyToOne
    private Subject subject;
}
