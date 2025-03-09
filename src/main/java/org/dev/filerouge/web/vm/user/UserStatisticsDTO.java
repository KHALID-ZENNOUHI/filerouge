package org.dev.filerouge.web.vm.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

/**
 * DTO for user statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsDTO {
    private long totalUsers;
    private long activeUsers;
    private long lockedUsers;
    private long disabledUsers;
    private Map<String, Long> usersByRole;
    private long usersWithTwoFactorEnabled;
    private long recentlyCreatedUsers;
    private long neverLoggedInUsers;
}
