package team.backend.curio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.backend.curio.domain.users;
import team.backend.curio.dto.CustomSettingDto;
import team.backend.curio.dto.UserCreateDto;
import team.backend.curio.dto.UserDTO.UserInterestResponse;
import team.backend.curio.dto.UserResponseDto;
import team.backend.curio.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 회원 가입 처리
    public UserResponseDto createUser(UserCreateDto userCreateDto) {
        // 1. 회원 가입을 위한 유저 객체 생성
        users newUser = new users();
        newUser.setNickname(userCreateDto.getNickname());
        newUser.setEmail(userCreateDto.getEmail());

        // 소셜 타입은 기본값 0 (일반 회원가입)
        newUser.setSocialType(0);

        users savedUser = userRepository.save(newUser);  // 여기서 userRepository 사용

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setUserId(savedUser.getUserId());  // user_id 반환
        responseDto.setNickname(savedUser.getNickname());
        responseDto.setEmail(savedUser.getEmail());

        return responseDto;
    }

    // 유저의 관심사 목록 조회
    public UserInterestResponse getUserInterests(Long userId) {
        users user = userRepository.findById(userId)
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
        users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 관심사 수정
        user.setInterest1(interests.get(0));
        user.setInterest2(interests.get(1));
        user.setInterest3(interests.get(2));
        user.setInterest4(interests.get(3));

        userRepository.save(user);

        return new UserInterestResponse(interests);
    }


    // 사용자의 커스텀 설정에서 요약 선호도 반환
    public CustomSettingDto getUserCustomSettings(Long userId) {
        users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 사용자가 설정한 요약 선호도 반환
        return new CustomSettingDto(user.getSummaryPreference());
    }


    // 사용자의 커스텀 설정에서 요약 선호도 및 기타 설정값 수정
    public CustomSettingDto updateUserCustomSettings(Long userId, CustomSettingDto customSettingDto) {
        users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 요약 선호도 수정
        user.setSummaryPreference(customSettingDto.getSummaryPreference());

        // 수신 이메일 저장
        user.setNewsletterEmail(customSettingDto.getNewsletterEmail());

        // 폰트 크기 저장 (String으로)
        user.setFontSize(customSettingDto.getFontSize());

        // 카테고리 리스트 → interest1~4에 분배
        List<String> categories = customSettingDto.getCategories();
        if (categories != null) {
            if (categories.size() > 0) user.setInterest1(categories.get(0));
            if (categories.size() > 1) user.setInterest2(categories.get(1));
            if (categories.size() > 2) user.setInterest3(categories.get(2));
            if (categories.size() > 3) user.setInterest4(categories.get(3));
        }

        userRepository.save(user);

        return new CustomSettingDto(
                user.getSummaryPreference(),
                user.getNewsletterEmail(),
                List.of(user.getInterest1(), user.getInterest2(), user.getInterest3(), user.getInterest4()),
                user.getFontSize()
        );
    }

    // 사용자의 닉네임과 프로필 사진 URL 반환
    public users getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // 사용자 정보 저장
    public void save(users user) {
        userRepository.save(user);  // user 객체를 DB에 저장
    }

    // 뉴스레터 신청 상태 업데이트
    public void updateNewsletterStatus(Long userId, boolean subscribe) {
        // 사용자 조회
        users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 뉴스레터 상태 업데이트 (1: 신청, 0: 취소)
        user.setNewsletterStatus(subscribe ? 1 : 0);

        // 변경된 사용자 정보 저장
        save(user);
    }
}