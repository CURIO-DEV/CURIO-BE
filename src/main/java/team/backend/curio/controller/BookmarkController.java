package team.backend.curio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import team.backend.curio.domain.Bookmark;
import team.backend.curio.dto.BookmarkDTO.CreateBookmarkDto;
import team.backend.curio.dto.BookmarkDTO.BookmarkResponseDto;
import team.backend.curio.dto.BookmarkDTO.NewsAddBookmarkDto;
import team.backend.curio.service.BookmarkService;

@RestController
@RequestMapping("/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Autowired
    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    // 북마크 폴더 생성
    @PostMapping("/create")
    public ResponseEntity<BookmarkResponseDto> createBookmark(@RequestBody CreateBookmarkDto createBookmarkDto) {
        // 북마크 생성
        BookmarkResponseDto bookmarkResponseDto = bookmarkService.createBookmark(createBookmarkDto);

        // 생성된 북마크 반환
        return new ResponseEntity<>(bookmarkResponseDto, HttpStatus.CREATED);
    }

    // 북마크 수정
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
    @DeleteMapping("/{bookmarkId}/delete")
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long bookmarkId) {
        bookmarkService.deleteBookmark(bookmarkId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // 북마크에 뉴스 추가
    @PostMapping("/{folderId}/news/add")
    public ResponseEntity<?> addNewsToBookmark(
            @PathVariable Long folderId,
            @RequestBody NewsAddBookmarkDto requestDto
    ) {
        bookmarkService.addNewsToBookmark(folderId, requestDto.getNewsId());
        return ResponseEntity.ok("뉴스가 북마크에 추가되었습니다.");
    }

    // 북마크에  뉴스 삭제
    @DeleteMapping("/{folderId}/news/{newsId}/remove")
    public ResponseEntity<?> removeNewsFromBookmark(
            @PathVariable Long folderId,
            @PathVariable Long newsId
    ) {
        bookmarkService.removeNewsFromBookmark(folderId, newsId);
        return ResponseEntity.ok("뉴스가 북마크에서 제거되었습니다.");
    }

}

