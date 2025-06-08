package team.backend.curio.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import team.backend.curio.domain.users;
import team.backend.curio.domain.News;
import team.backend.curio.dto.BookmarkDTO.MessageResponse;
import team.backend.curio.dto.CommonResponseDto;
import team.backend.curio.dto.NewsDTO.InterestNewsResponseDto;
import team.backend.curio.dto.NewsDTO.NewsResponseDto;
import team.backend.curio.dto.UserCreateDto;
import team.backend.curio.dto.CustomSettingDto;
import team.backend.curio.dto.UserDTO.NewsletterRequestDto;
import team.backend.curio.dto.UserDTO.UserInterestResponse;
import team.backend.curio.security.CustomUserDetails;
import team.backend.curio.service.UserService;
import team.backend.curio.service.NewsService;
import team.backend.curio.service.EmailService;
import team.backend.curio.service.TrendsService;
import team.backend.curio.dto.UserResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final TrendsService trendsService;
    private final EmailService emailService;

    public UserController(UserService userService, TrendsService trendsService, EmailService emailService) {
        this.userService = userService;
        this.trendsService = trendsService;
        this.emailService = emailService;
    }

    // 회원 가입
    /* @Operation(summary = "임시 회원 가입" )
    @PostMapping("/userCreate")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@RequestBody UserCreateDto userCreateDto) {
        return userService.createUser(userCreateDto);
    }*/

    @Operation(summary = "관심 카테고리 불러오기")
    @GetMapping("/interests")
    public ResponseEntity<UserInterestResponse> getInterests(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {
            UserInterestResponse response = userService.getUserInterests(userDetails.getUserId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 회원 관심사 수정하기
    @Operation(summary = "관심 카테고리 설정 및 수정")
    @PatchMapping("/interests")
    public ResponseEntity<UserInterestResponse> updateInterests(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody List<String> interests
    ) {
        if (interests == null || interests.size() != 4) {
            return ResponseEntity.badRequest().build();
        }
        try {
            UserInterestResponse updated = userService.updateUserInterests(userDetails.getUserId(), interests);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Autowired
    private NewsService newsService;

    // 유저의 관심사별 뉴스 목록 GET -> 유저아이디랑 관심사 이름 두개 필요
    @Operation(summary = "사용자의 특정 관심사 뉴스 목록 불러오기")
    @GetMapping("/interests/{interestName}/news")
    public ResponseEntity<List<NewsResponseDto>> getNewsByInterest(
            @PathVariable String interestName,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<NewsResponseDto> newsList = newsService.getNewsByInterestSortedByRecent(interestName)
                .stream()
                .map(NewsResponseDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(newsList);
    }

    // 유저의 관심사별 뉴스 목록 GET -> 유저아이디만 있으므로 4개 다 각각 출력
    /*@Operation(summary = "사용자별 뉴스 목록 불러오기")
    @GetMapping("{userId}/interests/news")
    public ResponseEntity<List<InterestNewsResponseDto>> getInterestNews(@PathVariable Long userId) {
        List<InterestNewsResponseDto> response = newsService.getInterestNewsByUserId(userId);
        return ResponseEntity.ok(response);
    }*/

    @Operation(summary = "사용자의 커스텀 설정 조회 (요약 선호도)")
    @GetMapping("/custom")
    public ResponseEntity<CustomSettingDto> getUserCustomSettings(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CustomSettingDto customSettingDto = userService.getUserCustomSettings(userDetails.getUserId());
        return ResponseEntity.ok(customSettingDto);
    }

    // 사용자 설정 전체 수정
    @Operation(summary = "사용자의 커스텀 설정 전체 수정 (요약, 이메일, 카테고리, 폰트크기)")
    @PatchMapping("/settings")
    public ResponseEntity<?> updateUserCustomSettings(
            @RequestBody CustomSettingDto customSettingDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {
            CustomSettingDto updated = userService.updateUserCustomSettings(userDetails.getUserId(), customSettingDto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 사용자 프로필 조회 (닉네임과 프로필 사진)
    @Operation(summary = "사용자 프로필 조회 (닉네임, 프로필 사진)")
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        users user = userService.getUserById(userDetails.getUserId());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        return ResponseEntity.ok(Map.of(
                "nickname", user.getNickname(),
                "profile_image_url", user.getProfile_image_url()
        ));
    }

    // 뉴스레터 수신 여부 설정 (가입된 회원만)
    @Operation(summary = "뉴스레터 신청")
    @PatchMapping("/newsletter/subscribe")
    public ResponseEntity<MessageResponse> updateNewsletterSubscription(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody NewsletterRequestDto requestDto
    ) {
        try {
            users user = userService.getUserById(userDetails.getUserId());

            user.setNewsletterStatus(1);
            user.setNewsletterEmail(requestDto.getNewsletterEmail());

            userService.save(user);

            return ResponseEntity.ok(new MessageResponse("뉴스레터가 신청되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("사용자를 찾을 수 없습니다."));
        }

    }

    // 최근 트렌드 뉴스 4개 가져오기
    private List<News> getTrendingNews() {
        // TrendsService에서 인기 뉴스 4개를 가져오는 메서드 호출
        return trendsService.getPopularArticles()
                .stream()
                .map(newsDto -> new News(newsDto))  // DTO를 News 객체로 변환
                .collect(Collectors.toList());
    }

    // 뉴스레터 발송 기능
    @Operation(summary = "뉴스레터 발송")
    @PatchMapping("/newsletter/send")
    public ResponseEntity<MessageResponse> sendNewsletter(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {
            users user = userService.getUserById(userDetails.getUserId());

            if (user.getNewsletterStatus() != 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse("뉴스레터를 신청한 사용자만 발송 가능합니다."));
            }

            List<News> trendingNews = getTrendingNews();
            emailService.sendNewsletter(user.getEmail(), trendingNews);

            return ResponseEntity.ok(new MessageResponse("뉴스레터가 발송되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("사용자를 찾을 수 없습니다."));
        }
    }

    @Operation(summary = "회원 탈퇴하기")
    @DeleteMapping("/delete")
    public ResponseEntity<CommonResponseDto<String>> deleteUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new CommonResponseDto<>(false, "로그인/회원가입을 해주세요", null));
        }

        Long authenticatedUserId = userDetails.getUserId();

        try {
            userService.deleteUser(authenticatedUserId);
            return ResponseEntity.ok(new CommonResponseDto<>(true, "회원 탈퇴가 완료되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CommonResponseDto<>(false, "회원 탈퇴 중 오류가 발생했습니다.", null));
        }
    }

    @GetMapping("/newsletter_email")
    public ResponseEntity<String> getNewsletterEmail(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        String newsletterEmail = userService.getNewsletterEmailByUserId(userId);
        return ResponseEntity.ok(newsletterEmail);
    }
}

