package team.backend.curio.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import team.backend.curio.domain.Bookmark;
import team.backend.curio.domain.News;
import team.backend.curio.dto.BookmarkDTO.CreateBookmarkDto;
import team.backend.curio.dto.BookmarkDTO.BookmarkResponseDto;
import team.backend.curio.dto.BookmarkDTO.NewsAddBookmarkDto;
import team.backend.curio.service.BookmarkService;

import java.util.List;

@RestController
@RequestMapping("/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Autowired
    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    // 북마크 폴더 생성
    @Operation(summary = "북마크 생성")
    @PostMapping("/create")
    public ResponseEntity<BookmarkResponseDto> createBookmark(@RequestBody CreateBookmarkDto createBookmarkDto) {
        BookmarkResponseDto bookmarkResponseDto = bookmarkService.createBookmark(createBookmarkDto);
        return new ResponseEntity<>(bookmarkResponseDto, HttpStatus.CREATED);
    }

    // 북마크 수정
    @Operation(summary = "북마크 정보 수정")
    @PatchMapping("/{bookmarkId}/update")
    public ResponseEntity<BookmarkResponseDto> updateBookmark(
            @PathVariable Long bookmarkId,
            @RequestBody CreateBookmarkDto updateDto) {

        Bookmark updatedBookmark = bookmarkService.updateBookmark(bookmarkId, updateDto);

        BookmarkResponseDto response = new BookmarkResponseDto(
                updatedBookmark.getId(),
                updatedBookmark.getName(),
                updatedBookmark.getColor()
        );

        return ResponseEntity.ok(response);
    }

    // 북마크 삭제
    @Operation(summary = "북마크 삭제")
    @DeleteMapping("/{bookmarkId}/delete")
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long bookmarkId) {
        bookmarkService.deleteBookmark(bookmarkId);
        return ResponseEntity.noContent().build(); // 204 No Content
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
    @Operation(summary = "북마크 목록 출력")
    @GetMapping("/list")
    public ResponseEntity<List<BookmarkResponseDto>> getBookmarkList() {
        List<Bookmark> bookmarks = bookmarkService.getAllBookmarks(); // 또는 로그인한 사용자 기준
        List<BookmarkResponseDto> result = bookmarks.stream()
                .map(bookmark -> new BookmarkResponseDto(
                        bookmark.getId(),
                        bookmark.getName(),
                        bookmark.getColor()
                )).toList();

        return ResponseEntity.ok(result);
    }

    // 북마크에 뉴스 리스트 출력
    @Operation(summary = "북마크별 뉴스 리스트 출력")
    @GetMapping("/{folderId}/news")
    public ResponseEntity<List<News>> getNewsByBookmark(@PathVariable Long folderId) {
        Bookmark bookmark = bookmarkService.getBookmarkById(folderId);
        List<News> newsList = bookmark.getNewsList(); // ManyToMany로 매핑된 리스트
        return ResponseEntity.ok(newsList);
    }

}

