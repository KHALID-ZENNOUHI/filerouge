package org.dev.filerouge.web.vm.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dev.filerouge.domain.Enum.Role;

/**
 * DTO for UserRole counts
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleCountDTO {
    private Role role;
    private long count;
}
