package team.backend.curio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.backend.curio.client.KakaoOAuthClient;
import team.backend.curio.domain.users;
import team.backend.curio.dto.CustomSettingDto;
import team.backend.curio.dto.UserCreateDto;
import team.backend.curio.dto.UserDTO.UserInterestResponse;
import team.backend.curio.dto.UserResponseDto;
import team.backend.curio.repository.UserRepository;
import team.backend.curio.dto.setting.CustomSettingPatchResponseDto;
import team.backend.curio.dto.setting.CustomSettingRequestDto;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final NewsService newsService; // NewsService로부터 인기 뉴스 데이터를 가져옵니다.
    private final KakaoOAuthClient kakaoOAuthClient;

    @Autowired
    public UserService(UserRepository userRepository, EmailService emailService, NewsService newsService, KakaoOAuthClient kakaoOAuthClient) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.newsService = newsService;
        this.kakaoOAuthClient = kakaoOAuthClient;
    }

    private int mapSummaryPreference(String summaryType) {
        if (summaryType == null) return 2; // 기본값: medium
        return switch (summaryType.toLowerCase()) {
            case "short" -> 1;
            case "medium" -> 2;
            case "long" -> 3;
            default -> 2;
        };
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

        //기본카테고리
        List<String> defaults = List.of("사회", "정치", "경제", "연예");
        List<String> interests = new ArrayList<>();

        // 각 관심사가 null이면 기본값으로 대체
        interests.add(user.getInterest1() != null ? user.getInterest1() : defaults.get(0));
        interests.add(user.getInterest2() != null ? user.getInterest2() : defaults.get(1));
        interests.add(user.getInterest3() != null ? user.getInterest3() : defaults.get(2));
        interests.add(user.getInterest4() != null ? user.getInterest4() : defaults.get(3));

        // 기본값 적용 후 응답
        return new UserInterestResponse(interests);
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

        // summaryPreference → summaryType 변환
        String summaryType = switch (user.getSummaryPreference()) {
            case 1 -> "short";
            case 3 -> "long";
            default -> "medium"; // null 또는 2
        };

        // 카테고리 4개 → null일 경우 기본값
        List<String> defaultCategories = List.of("사회", "정치", "경제", "연예");
        List<String> responseCategories = List.of(
                user.getInterest1() != null ? user.getInterest1() : defaultCategories.get(0),
                user.getInterest2() != null ? user.getInterest2() : defaultCategories.get(1),
                user.getInterest3() != null ? user.getInterest3() : defaultCategories.get(2),
                user.getInterest4() != null ? user.getInterest4() : defaultCategories.get(3)
        );

        // fontSize 기본값 처리
        String fontSize = user.getFontSize() != null ? user.getFontSize() : "medium";

        // 뉴스레터 수신 상태 및 이메일 처리
        boolean receiveNewsletter = user.getNewsletterStatus() == 1;
        String newsletterEmail = user.getNewsletterEmail() != null
                ? user.getNewsletterEmail()
                : user.getEmail(); // 회원가입한 이메일을 기본값으로

        return new CustomSettingDto(
                summaryType,
                newsletterEmail,
                receiveNewsletter,
                responseCategories,
                fontSize,
                user.getSocialType()
        );

    }


    // 사용자의 커스텀 설정에서 요약 선호도 및 기타 설정값 수정
    public CustomSettingPatchResponseDto updateUserCustomSettings(Long userId, CustomSettingDto customSettingDto) {
        users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // summaryType → summaryPreference 매핑
        int preference = mapSummaryPreference(customSettingDto.getSummaryType());
        user.setSummaryPreference(preference);

        // 수신 이메일 저장
        user.setNewsletterEmail(customSettingDto.getNewsletterEmail());

        // 수신 상태 설정
        user.setNewsletterStatus(customSettingDto.isReceiveNewsletter() ? 1 : 0);

        // 폰트 크기 저장 (String으로)
        user.setFontSize(customSettingDto.getFontSize());

        // 프론트에서 넘긴 관심사 저장 (최대 4개)
        List<String> categories = customSettingDto.getCategories();
        if (categories != null) {
            categories = categories.stream()
                    .filter(c -> !"string".equalsIgnoreCase(c))  // 혹시 모르니 방어
                    .collect(Collectors.toList());
            if (categories.size() > 0) user.setInterest1(categories.get(0));
            if (categories.size() > 1) user.setInterest2(categories.get(1));
            if (categories.size() > 2) user.setInterest3(categories.get(2));
            if (categories.size() > 3) user.setInterest4(categories.get(3));
        }

        // 관심사 4개를 null 여부에 따라 기본값으로 대체
        List<String> defaultCategories = List.of("사회", "정치", "경제", "연예");
        List<String> responseCategories = new ArrayList<>();
        responseCategories.add(user.getInterest1() != null ? user.getInterest1() : defaultCategories.get(0));
        responseCategories.add(user.getInterest2() != null ? user.getInterest2() : defaultCategories.get(1));
        responseCategories.add(user.getInterest3() != null ? user.getInterest3() : defaultCategories.get(2));
        responseCategories.add(user.getInterest4() != null ? user.getInterest4() : defaultCategories.get(3));


        userRepository.save(user);

        return new CustomSettingPatchResponseDto(
                customSettingDto.getSummaryType() != null ? customSettingDto.getSummaryType() : "medium",
                user.getNewsletterEmail(),
                user.getNewsletterStatus()==1,
                responseCategories,
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

    // 뉴스레터 수신 설정된 사용자 조회
    public List<users> getUsersWithNewsletterSubscribed() {
        return userRepository.findByNewsletterStatusAndNewsletterEmailNotNull(1); // 1은 뉴스레터 구독 상태
    }

    public String deleteUser(Long userId){
        //유저가 존재하는지 확인
        users user=userRepository.findById(userId).orElse(null);
        if(user ==null){
            return "User not found"; //유저가 없다면 메시지 반환
        }

        // ✅ 카카오 연결 끊기 (소셜 로그인인 경우만)
        if (user.getSocialType() == 1) {
            String oauthId = user.getOauthId();
            if (oauthId != null) {
                kakaoOAuthClient.unlink(oauthId);
            } else {
                System.err.println("❗️카카오 유저지만 oauthId가 null입니다. unlink 스킵");
            }
        }

        //유저 삭제
        userRepository.delete(user);

        return "User가 성공적으로 삭제되었습니다."; //성공 메세지 반환
    }

    public String getNewsletterEmailByUserId(Long userId) {
        users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String newsletterEmail = user.getNewsletterEmail();
        if (newsletterEmail == null || newsletterEmail.isBlank()) {
            return user.getEmail();
        }
        return newsletterEmail;
    }

}