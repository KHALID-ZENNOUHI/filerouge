package org.dev.filerouge.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @NotBlank
    private String name;

    @ManyToOne
    private Level level;
}
