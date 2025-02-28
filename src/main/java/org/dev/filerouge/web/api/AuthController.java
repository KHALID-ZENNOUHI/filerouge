package org.dev.filerouge.web.api;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.dev.filerouge.domain.User;
import org.dev.filerouge.service.implementation.MyUserService;
import org.dev.filerouge.web.vm.LoginVM;
import org.dev.filerouge.web.vm.ProfileVM;
import org.dev.filerouge.web.vm.RegisterVM;
import org.dev.filerouge.web.vm.mapper.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserMapper userMapper;
    private final MyUserService userService;

    public AuthController(UserMapper userMapper, MyUserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginVM loginVM) {
        User loginToUser = userMapper.loginToUser(loginVM);
        String jwt = userService.login(loginToUser);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/register")
    public ResponseEntity<ProfileVM> register(@Valid @RequestBody RegisterVM registerVM) {
        User user = userMapper.registerToUser(registerVM);
        userService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
