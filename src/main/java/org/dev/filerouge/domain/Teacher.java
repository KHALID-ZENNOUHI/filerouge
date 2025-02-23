package org.dev.filerouge.domain;

import jakarta.persistence.OneToMany;
import lombok.*;
import jakarta.persistence.Entity;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Teacher extends User {
    @OneToMany(mappedBy = "teacher")
    private Set<Session> sessions;
}
