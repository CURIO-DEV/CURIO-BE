package team.backend.curio.controller;

import io.swagger.v3.oas.annotations.Operation;
import team.backend.curio.domain.News;
import team.backend.curio.dto.NewsDTO.InterestNewsResponseDto;
import team.backend.curio.dto.NewsDTO.NewsResponseDto;
import team.backend.curio.dto.UserCreateDto;
import team.backend.curio.dto.CustomSettingDto;
import team.backend.curio.dto.UserDTO.UserInterestResponse;
import team.backend.curio.service.UserService;
import team.backend.curio.service.NewsService;
import team.backend.curio.dto.UserResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 회원 가입
    @Operation(summary = "임시 회원 가입" /*,description = "새로운 유저를 등록합니다."*/)
    @PostMapping("/userCreate")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@RequestBody UserCreateDto userCreateDto) {
        return userService.createUser(userCreateDto);
    }

    @Operation(summary = "관심 카테고리 불러오기")
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
    @Operation(summary = "관심 카테고리 설정 및 수정")
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

    @Autowired
    private NewsService newsService;

    // 유저의 관심사별 뉴스 목록 GET -> 유저아이디랑 관심사 이름 두개 필요
    @Operation(summary = "사용자의 특정 관심사 뉴스 목록 불러오기")
    @GetMapping("{userId}/interests/{interestName}/news")
    public List<NewsResponseDto> getNewsByInterest(@PathVariable int userId, @PathVariable String interestName) {
        return newsService.getNewsByInterest(interestName)
                .stream()
                .map(NewsResponseDto::new)
                .collect(Collectors.toList());
    }

    // 유저의 관심사별 뉴스 목록 GET -> 유저아이디만 있으므로 4개 다 각각 출력
    @Operation(summary = "사용자별 뉴스 목록 불러오기")
    @GetMapping("{userId}/interests/news")
    public ResponseEntity<List<InterestNewsResponseDto>> getInterestNews(@PathVariable Long userId) {
        List<InterestNewsResponseDto> response = newsService.getInterestNewsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자의 커스텀 설정 조회 (요약 선호도)")
    @GetMapping("/{userId}/custom")
    public ResponseEntity<CustomSettingDto> getUserCustomSettings(@PathVariable Long userId){
        CustomSettingDto customSettingDto= userService.getUserCustomSettings(userId); // 유저 서비스 호출하여 데이터 가져오기
        return ResponseEntity.ok(customSettingDto);
    }

    // summaryPreference 값을 short, medium, long으로 매핑하는 함수 추가
    private String mapSummaryType(int summaryPreference) {
        switch (summaryPreference) {
            case 1:
                return "short";
            case 2:
                return "medium";
            case 3:
                return "long";
            default:
                return "medium"; // 기본값은 "medium"으로 설정
        }
    }
}

