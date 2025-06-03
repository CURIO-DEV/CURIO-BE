package team.backend.curio.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import team.backend.curio.domain.Bookmark;
import team.backend.curio.domain.News;
import team.backend.curio.domain.users;
import team.backend.curio.dto.BookmarkDTO.CreateBookmarkDto;
import team.backend.curio.dto.BookmarkDTO.BookmarkResponseDto;
import team.backend.curio.dto.BookmarkDTO.NewsAddBookmarkDto;
import team.backend.curio.dto.NewsDTO.NewsResponseDto;
import team.backend.curio.repository.UserRepository;
import team.backend.curio.service.BookmarkService;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final UserRepository userRepository;

    @Autowired
    public BookmarkController(BookmarkService bookmarkService, UserRepository userRepository) {
        this.bookmarkService = bookmarkService;
        this.userRepository = userRepository;
    }

    // 북마크 폴더 생성
    @Operation(summary = "북마크 생성")
    @PostMapping("/create")
    public ResponseEntity<BookmarkResponseDto> createBookmark(
            @RequestBody CreateBookmarkDto createBookmarkDto,  // 본문에 있는 JSON 파라미터
            @RequestParam String email) {  // 쿼리 파라미터로 받는 email

        // 서비스 메서드 호출
        BookmarkResponseDto response = bookmarkService.createBookmark(createBookmarkDto, email);
        return ResponseEntity.ok(response);
    }

    // 북마크 수정
    @Operation(summary = "북마크 정보 수정")
    @PatchMapping("/{bookmarkId}/update")
    public ResponseEntity<BookmarkResponseDto> updateBookmark(
            @PathVariable Long bookmarkId,
            @RequestBody CreateBookmarkDto updateDto,
            @RequestParam String email) {  // 이메일 파라미터 추가

        Bookmark updatedBookmark = bookmarkService.updateBookmark(bookmarkId, updateDto, email); // 이메일 전달

        // 공동작업자 이메일 리스트 생성
        List<String> memberEmails = updatedBookmark.getMembers().stream()
                .map(users -> users.getEmail())
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
    public ResponseEntity<String> leaveBookmark(
            @PathVariable Long bookmarkId,
            @RequestParam String email
    ) {
        bookmarkService.deleteBookmarkForUser(bookmarkId, email);
        return ResponseEntity.ok("북마크에서 나갔습니다.");
    }

    // 북마크에 뉴스 추가
    @Operation(summary = "북마크에 뉴스 추가하기")
    @PostMapping("/{folderId}/news/add")
    public ResponseEntity<?> addNewsToBookmark(
            @PathVariable Long folderId,
            @RequestBody NewsAddBookmarkDto requestDto
    ) {
        bookmarkService.addNewsToBookmark(folderId, requestDto.getNewsId());
        return ResponseEntity.ok("뉴스가 북마크에 추가되었습니다.");
    }

    // 북마크에  뉴스 삭제
    @Operation(summary = "북마크에 있는 뉴스 삭제하기")
    @DeleteMapping("/{folderId}/news/{newsId}/remove")
    public ResponseEntity<?> removeNewsFromBookmark(
            @PathVariable Long folderId,
            @PathVariable Long newsId
    ) {
        bookmarkService.removeNewsFromBookmark(folderId, newsId);
        return ResponseEntity.ok("뉴스가 북마크에서 제거되었습니다.");
    }

    // 북마크 목록 출력
    @Operation(summary = "특정 유저의 북마크 목록 출력")
    @GetMapping("/{userId}/list")
    public ResponseEntity<List<BookmarkResponseDto>> getBookmarksByUser(@PathVariable Long userId) {
        users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Bookmark> bookmarks = user.getBookmarks(); // 또는 서비스로 분리 가능

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
    public List<NewsResponseDto> getNewsByBookmark(@PathVariable("folderId") Long folderId) {
        Bookmark bookmark = bookmarkService.getBookmarkById(folderId);
        List<News> newsList = bookmark.getNewsList();

        return newsList.stream()
                .map(news -> new NewsResponseDto(news))
                .collect(Collectors.toList());
    }
}

