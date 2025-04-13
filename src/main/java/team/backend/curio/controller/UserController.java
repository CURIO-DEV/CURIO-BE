package team.backend.curio.controller;

import team.backend.curio.dto.UserCreateDto;
import team.backend.curio.service.UserService;
import team.backend.curio.dto.UserResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")

public class UserController {

    @Autowired
    private UserService userService;

    // 회원 가입
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@RequestBody UserCreateDto userCreateDto) {
        return userService.createUser(userCreateDto);
    }
}
