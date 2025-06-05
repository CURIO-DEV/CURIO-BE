package team.backend.curio.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import team.backend.curio.domain.Bookmark;
import team.backend.curio.domain.News;
import team.backend.curio.domain.users;
import team.backend.curio.dto.BookmarkDTO.*;
import team.backend.curio.dto.NewsDTO.NewsResponseDto;
import team.backend.curio.exception.DuplicateNewsInBookmarkException;
import team.backend.curio.repository.UserRepository;
import team.backend.curio.security.CustomUserDetails;
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
    public ResponseEntity<MessageResponse> leaveBookmark(
            @PathVariable Long bookmarkId,
            @RequestParam String email
    ) {
        bookmarkService.deleteBookmarkForUser(bookmarkId, email);
        return ResponseEntity.ok(new MessageResponse("해당 북마크를 목록에서 삭제했습니다."));
    }

    // 북마크에 뉴스 추가
    @Operation(summary = "북마크에 뉴스 추가하기")
    @PostMapping("/{folderId}/news/add")
    public ResponseEntity<?> addNewsToBookmark(
            @PathVariable Long folderId,
            @RequestBody NewsAddBookmarkDto requestDto
    ) {
        try {
            bookmarkService.addNewsToBookmark(folderId, requestDto.getArticleId());
            return ResponseEntity.ok(new MessageResponse("뉴스가 북마크에 추가되었습니다."));
        } catch (DuplicateNewsInBookmarkException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    // 북마크에  뉴스 삭제
    @Operation(summary = "북마크에 있는 뉴스 삭제하기")
    @DeleteMapping("/{folderId}/news/{newsId}/remove")
    public ResponseEntity<?> removeNewsFromBookmark(
            @PathVariable Long folderId,
            @PathVariable Long newsId
    ) {
        bookmarkService.removeNewsFromBookmark(folderId, newsId);
        return ResponseEntity.ok(new MessageResponse("뉴스가 북마크에서 삭제되었습니다."));
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
    public List<BookmarkNewsDto> getNewsByBookmark(@PathVariable("folderId") Long folderId) {
        Bookmark bookmark = bookmarkService.getBookmarkById(folderId);
        List<News> newsList = bookmark.getNewsList();

        return newsList.stream()
                .map(news -> new BookmarkNewsDto(news))
                .collect(Collectors.toList());
    }
}

