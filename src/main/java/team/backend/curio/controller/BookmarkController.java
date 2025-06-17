package team.backend.curio.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import team.backend.curio.domain.Bookmark;
import team.backend.curio.domain.News;
import team.backend.curio.domain.users;
import team.backend.curio.dto.BookmarkDTO.*;
import team.backend.curio.dto.NewsDTO.NewsResponseDto;
import team.backend.curio.exception.DuplicateNewsInBookmarkException;
import team.backend.curio.repository.BookmarkRepository;
import team.backend.curio.repository.UserRepository;
import team.backend.curio.security.CustomUserDetails;
import team.backend.curio.service.BookmarkService;
import team.backend.curio.service.GptSummaryService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static team.backend.curio.domain.QBookmark.bookmark;


@RestController
@RequestMapping("/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final GptSummaryService gptSummaryService;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;


    @Autowired
    public BookmarkController(BookmarkService bookmarkService, UserRepository userRepository, BookmarkRepository bookmarkRepository, GptSummaryService gptSummaryService) {
        this.bookmarkService = bookmarkService;
        this.userRepository = userRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.gptSummaryService = gptSummaryService;
    }

    // 북마크 폴더 생성
    @Operation(summary = "북마크 생성")
    @PostMapping("/create")
    public ResponseEntity<BookmarkResponseDto> createBookmark(
            @RequestBody CreateBookmarkDto createBookmarkDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUserId();
        BookmarkResponseDto response = bookmarkService.createBookmark(createBookmarkDto, userId);

        return ResponseEntity.ok(response);
    }

    // 북마크 수정
    @Operation(summary = "북마크 정보 수정")
    @PatchMapping("/{bookmarkId}/update")
    public ResponseEntity<BookmarkResponseDto> updateBookmark(
            @PathVariable Long bookmarkId,
            @RequestBody CreateBookmarkDto updateDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUserId();
        Bookmark updatedBookmark = bookmarkService.updateBookmark(bookmarkId, updateDto, userId);

        String currentUserEmail = userDetails.getEmail();

        // 공동작업자 이메일 리스트 생성
        List<String> memberEmails = updatedBookmark.getMembers().stream()
                .map(users::getEmail)
                .filter(email -> !email.equals(currentUserEmail))  // 현재 유저 이메일 제외
                .toList();

        BookmarkResponseDto response = new BookmarkResponseDto(
                updatedBookmark.getId(),
                updatedBookmark.getName(),
                updatedBookmark.getColor(),
                memberEmails
        );

        return ResponseEntity.ok(response);
    }


    // 북마크 삭제
    @Operation(summary = "내 북마크에서만 나가기")
    @DeleteMapping("/{bookmarkId}/delete")
    public ResponseEntity<?> leaveBookmark(
            @PathVariable Long bookmarkId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String userEmail = userDetails.getEmail();

        try {
            bookmarkService.deleteBookmarkForUser(bookmarkId, userEmail);
            return ResponseEntity.ok(new MessageResponse("해당 북마크를 목록에서 삭제했습니다."));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // 북마크에 뉴스 추가
    @Operation(summary = "북마크에 뉴스 추가하기")
    @PostMapping("/{folderId}/news/{newsId}")
    public ResponseEntity<Map<String, String>> addNewsToBookmark(
            @PathVariable Long folderId,
            @PathVariable Long newsId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        users currentUser = userRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        try {
            bookmarkService.addNewsToBookmark(folderId, newsId, currentUser);
            return ResponseEntity.ok(Map.of("message", "뉴스가 북마크에 추가되었습니다."));
        } catch (DuplicateNewsInBookmarkException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        }
    }


    // 북마크에  뉴스 삭제
    @Operation(summary = "북마크에 있는 뉴스 삭제하기")
    @DeleteMapping("/{folderId}/news/{newsId}")
    public ResponseEntity<Map<String, String>> removeNewsFromBookmark(
            @PathVariable Long folderId,
            @PathVariable Long newsId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        users currentUser = userRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        try {
            bookmarkService.removeNewsFromBookmark(folderId, newsId, currentUser);
            return ResponseEntity.ok(Map.of("message", "뉴스가 북마크에서 삭제되었습니다."));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // 북마크 목록 출력
    @Operation(summary = "특정 유저의 북마크 목록 출력")
    @GetMapping("/list")
    public ResponseEntity<List<BookmarkResponseDto>>
    getBookmarksByUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다"));

        List<Bookmark> bookmarks = user.getBookmarks();

        List<BookmarkResponseDto> result = bookmarks.stream()
                .map(bookmark -> new BookmarkResponseDto(
                        bookmark.getId(),
                        bookmark.getName(),
                        bookmark.getColor(),
                        bookmark.getMembers().stream().map(users::getEmail).toList()
                )).toList();

        return ResponseEntity.ok(result);

    }


    // 북마크에 뉴스 리스트 출력
    @Operation(summary = "북마크별 뉴스 리스트 출력")
    @GetMapping("/{folderId}/news")
    public ResponseEntity<?> getNewsListForBookmark(
            @PathVariable Long folderId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {
            // 1. 액세스 토큰으로부터 userId 또는 email 바로 꺼내기
            String userEmail = userDetails.getEmail();

            // 2. folderId로 북마크 가져오기
            Bookmark bookmark = bookmarkRepository.findById(folderId)
                    .orElseThrow(() -> new RuntimeException("북마크가 존재하지 않습니다."));

            // 3. 북마크 참여자 이메일 리스트 가져오기 (users 객체 대신 이메일 비교)
            boolean isMember = bookmark.getMembers().stream()
                    .anyMatch(member -> member.getEmail().equals(userEmail));

            if (!isMember) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "본인의 북마크가 아닙니다."));
            }

            // 4. 뉴스 리스트를 DTO로 변환
            List<BookmarkNewsDto> newsDtoList = bookmark.getNewsList().stream()
                    .map(BookmarkNewsDto::new)
                    .toList();
            return ResponseEntity.ok(newsDtoList);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "북마크 뉴스 요약 및 핵심 키워드 생성")
    @GetMapping("/{bookmarkId}/summary")
    public ResponseEntity<?> getBookmarkSummary(
            @PathVariable Long bookmarkId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {
            // 1. 사용자 인증
            String userEmail = userDetails.getEmail();

            // 2. 북마크 조회
            Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                    .orElseThrow(() -> new RuntimeException("북마크가 존재하지 않습니다."));

            // 3. 사용자 권한 확인
            boolean isMember = bookmark.getMembers().stream()
                    .anyMatch(member -> member.getEmail().equals(userEmail));
            if (!isMember) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "본인의 북마크가 아닙니다."));
            }

            // 4. 뉴스 요약 내용 추출
            List<String> summaries = bookmark.getNewsList().stream()
                    .map(News::getSummaryMedium)
                    .filter(Objects::nonNull)
                    .toList();

            if (summaries.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(Map.of("message", "요약된 뉴스가 없습니다."));
            }

            // GPT 프롬프트 구성
            String joinedSummaries = String.join("\n", summaries);
            String prompt = """
                    다음은 북마크 안에 저장된 뉴스들의 요약 내용입니다. 이 뉴스들을 분석하고, 이 북마크가 어떤 주제들을 다루는지 아래 형식에 맞게 요약해주세요.

                    형식:
                    이 북마크는 __________ 에 대한 내용을 중심으로 다루고 있습니다.
                    핵심 키워드: 키워드1, 키워드2, 키워드3, 키워드4

                    ※ 다양한 주제를 다룰 경우, 가장 주요한 흐름을 중심으로 설명해주세요.
                    ※ 키워드는 내용의 핵심 개념을 요약한 단어들입니다.

                    뉴스 요약:
                    """ + joinedSummaries;

            // GPT 호출
            String result = gptSummaryService.callGptApi(prompt);

            return ResponseEntity.ok(Map.of("summary", result));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }


}

