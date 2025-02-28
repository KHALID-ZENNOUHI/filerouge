package org.dev.filerouge.domain;

import lombok.*;
import jakarta.persistence.*;


@Entity
@Table(name = "administrators")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue("ADMINISTRATOR")
@Getter
@Setter
@NoArgsConstructor
public class Administrator extends User {

}
