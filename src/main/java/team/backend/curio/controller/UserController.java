package team.backend.curio.controller;

import team.backend.curio.dto.UserCreateDto;
import team.backend.curio.dto.UserDTO.UserInterestResponse;
import team.backend.curio.service.UserService;
import team.backend.curio.dto.UserResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 회원 가입
    @PostMapping("/userCreate")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@RequestBody UserCreateDto userCreateDto) {
        return userService.createUser(userCreateDto);
    }

    @GetMapping("/{userId}/interests")
    public ResponseEntity<UserInterestResponse> getInterests(@PathVariable Long userId) {
        try {
            UserInterestResponse response = userService.getUserInterests(userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 회원 관심사 수정하기
    @PatchMapping("/{userId}/interests")
    public ResponseEntity<UserInterestResponse> updateInterests(@PathVariable Long userId, @RequestBody List<String> interests) {
        if (interests == null || interests.size() != 4) {
            return ResponseEntity.badRequest().build(); // 관심사는 4개여야 함
        }
        try {
            UserInterestResponse updatedInterests = userService.updateUserInterests(userId, interests);
            return ResponseEntity.ok(updatedInterests);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}

