package team.backend.curio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.backend.curio.domain.News;
import team.backend.curio.domain.users;
import team.backend.curio.dto.UserCreateDto;
import team.backend.curio.dto.UserDTO.UserInterestResponse;
import team.backend.curio.dto.UserResponseDto;
import team.backend.curio.repository.UserRepository;


import java.util.List;

@Service
public class UserService {

    private final UserRepository UserRepository;
    private final NewsService NewsService;

    @Autowired
    public UserService(UserRepository userRepository, NewsService newsService) {
        this.UserRepository = userRepository;
        this.NewsService = newsService;
    }

    // 회원 가입 처리
    public UserResponseDto createUser(UserCreateDto UserCreateDto) {
        // 1. 회원 가입을 위한 유저 객체 생성
        users newUser = new users();
        newUser.setNickname(UserCreateDto.getNickname());
        newUser.setEmail(UserCreateDto.getEmail());

        // 소셜 타입은 기본값 0 (일반 회원가입)
        newUser.setSocialType(0);

        users savedUser = UserRepository.save(newUser);  // 여기서 userRepository 사용

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

    // 관심사별 뉴스 조회
    public List<News> getNewsByInterest(Long userId, String interestName) {
        // 유저의 관심사 목록을 가져옴
        users user = UserRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        String category = null;
        if (user.getInterest1().equals(interestName)) {
            category = interestName;
        } else if (user.getInterest2().equals(interestName)) {
            category = interestName;
        } else if (user.getInterest3().equals(interestName)) {
            category = interestName;
        } else if (user.getInterest4().equals(interestName)) {
            category = interestName;
        }

        if (category == null) {
            throw new IllegalArgumentException("Interest not found in user's interests");
        }

        // NewsService를 사용하여 관심사에 맞는 뉴스 목록을 가져옴
        return NewsService.getNewsByInterest(category);
    }

    // 뉴스 목록을 가져오는 메소드
    public List<News> getAllNews() {
        return NewsService.getAllNews();
    }

}
