package org.dev.filerouge.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "parents")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("PARENT")
@Getter
@Setter
@NoArgsConstructor
public class Parent extends User {
}
