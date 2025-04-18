package team.backend.curio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.backend.curio.domain.users;
import team.backend.curio.dto.UserCreateDto;
import team.backend.curio.dto.UserDTO.UserInterestResponse;
import team.backend.curio.dto.UserResponseDto;
import team.backend.curio.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository UserRepository;

    @Autowired
    public UserService(UserRepository UserRepository) {
        this.UserRepository = UserRepository;
    }

    // 회원 가입 처리
    public UserResponseDto createUser(UserCreateDto UserCreateDto) {
        // 1. 회원 가입을 위한 유저 객체 생성
        users newUser = new users();
        newUser.setNickname(UserCreateDto.getNickname());
        newUser.setEmail(UserCreateDto.getEmail());

        // 소셜 타입은 기본값 0 (일반 회원가입)
        newUser.setSocialType(0);

        // 2. DB에 저장
        users savedUser = UserRepository.save(newUser);  // 여기서 userRepository 사용

        // 3. 응답 DTO로 반환 (user_id, nickname, email 반환)
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setUserId(savedUser.getUserId());  // user_id 반환
        responseDto.setNickname(savedUser.getNickname());
        responseDto.setEmail(savedUser.getEmail());

        return responseDto;
    }

    public UserInterestResponse getUserInterests(Long userId) {
        users user = UserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        if (user.getInterest1() == null || user.getInterest2() == null ||
                user.getInterest3() == null || user.getInterest4() == null) {
            return new UserInterestResponse(List.of("사회", "정치", "경제", "연예")); // 기본값
        }

        return new UserInterestResponse(List.of(
                user.getInterest1(),
                user.getInterest2(),
                user.getInterest3(),
                user.getInterest4()
        ));
    }

    // 회원 관심사 수정
    public UserInterestResponse updateUserInterests(Long userId, List<String> interests) {
        users user = UserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 관심사 수정
        user.setInterest1(interests.get(0));
        user.setInterest2(interests.get(1));
        user.setInterest3(interests.get(2));
        user.setInterest4(interests.get(3));

        UserRepository.save(user);

        return new UserInterestResponse(interests);
    }



}
