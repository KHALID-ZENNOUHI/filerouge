package org.dev.filerouge.web.vm.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for updating a user's status
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusUpdateDTO {
    @NotNull
    private Boolean enabled;

    @NotNull
    private Boolean locked;

    private String reasonForChange;
}
