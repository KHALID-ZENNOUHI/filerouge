package org.dev.filerouge.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    private String name;

    @ManyToOne
    private Department department;
}
