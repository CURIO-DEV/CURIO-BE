package team.backend.curio.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.backend.curio.service.BookmarkService;
import team.backend.curio.domain.Bookmark;

@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    // 북마크 폴더 생성
    @PostMapping("/create")
    public ResponseEntity<?> createBookmark(@RequestBody Bookmark bookmark) {
        Bookmark createdBookmark = bookmarkService.createBookmark(bookmark);
        return ResponseEntity.ok(createdBookmark);
    }

    // 추가로 폴더 수정, 삭제, 기사 추가 등등도 차차 만들면 돼
}
