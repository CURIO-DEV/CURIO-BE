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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static team.backend.curio.domain.QBookmark.bookmark;


@RestController
@RequestMapping("/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;


    @Autowired
    public BookmarkController(BookmarkService bookmarkService, UserRepository userRepository, BookmarkRepository bookmarkRepository) {
        this.bookmarkService = bookmarkService;
        this.userRepository = userRepository;
        this.bookmarkRepository = bookmarkRepository;
    }

    private users getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (users) authentication.getPrincipal();
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

        // 공동작업자 이메일 리스트 생성
        List<String> memberEmails = updatedBookmark.getMembers().stream()
                .map(users::getEmail).toList();

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

            // 4. 권한 확인 통과 시 뉴스 리스트 반환
            List<News> newsList = bookmark.getNewsList();
            return ResponseEntity.ok(newsList);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}

