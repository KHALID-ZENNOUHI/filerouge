package org.dev.filerouge.web.vm.mapper;

import org.dev.filerouge.domain.User;
import org.dev.filerouge.web.vm.LoginVM;
import org.dev.filerouge.web.vm.ProfileVM;
import org.dev.filerouge.web.vm.RegisterVM;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User loginToUser(LoginVM loginVM);
    User registerToUser(RegisterVM registerVM);
    ProfileVM userToProfile(User user);
}

