package team.backend.curio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.backend.curio.domain.User;
import team.backend.curio.dto.UserCreateDto;
import team.backend.curio.dto.UserResponseDto;
import team.backend.curio.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 회원 가입 처리
    public UserResponseDto createUser(UserCreateDto userCreateDto) {
        // 1. 회원 가입을 위한 유저 객체 생성
        User newUser = new User();
        newUser.setNickname(userCreateDto.getNickname());
        newUser.setEmail(userCreateDto.getEmail());

        // 2. DB에 저장
        User savedUser = userRepository.save(newUser);

        // 3. 응답 DTO로 반환 (user_id, nickname, email 반환)
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setUserId(savedUser.getUserId());  // user_id 반환
        responseDto.setNickname(savedUser.getNickname());
        responseDto.setEmail(savedUser.getEmail());

        return responseDto;
    }
}
