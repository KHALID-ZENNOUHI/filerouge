package org.dev.filerouge.web.vm;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import org.dev.filerouge.domain.Enum.Gender;
import org.dev.filerouge.domain.Enum.Role;

import java.time.LocalDate;

@Getter
@Setter
public class ProfileVM {
    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String cin;

    private String phone;

    private LocalDate birthDate;

    private String birthPlace;

    private String address;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String photo;

    @Enumerated(EnumType.STRING)
    private Role role;
}
