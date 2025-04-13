package team.backend.curio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.backend.curio.repository.UserRepository;
import team.backend.curio.dto.UserCreateDto;
import team.backend.curio.dto.UserResponseDto;
import team.backend.curio.domain.User;

import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 랜덤 닉네임 생성 메서드
    private String generateRandomNickname() {
        String[] adjectives = {"빠른", "친절한", "용감한", "신속한", "슬기로운"};
        String[] animals = {"호랑이", "고양이", "사자", "부엉이", "곰"};

        Random rand = new Random();
        String adjective = adjectives[rand.nextInt(adjectives.length)];
        String animal = animals[rand.nextInt(animals.length)];

        return adjective + animal;
    }

    @Transactional
    public UserResponseDto createUser(UserCreateDto userCreateDto) {
        // 랜덤 닉네임 생성
        String nickname = generateRandomNickname();

        // 새로운 유저 생성
        User user = new User();
        user.setSocialType(userCreateDto.getSocialType());
        user.setNewsletterEmail(userCreateDto.getNewsletterEmail());
        user.setNickname(nickname);

        // DB에 저장
        User savedUser = userRepository.save(user);

        // 반환할 DTO로 변환하여 반환
        return new UserResponseDto(savedUser.getUserId(), savedUser.getNickname());
    }
}
