package team.backend.curio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import team.backend.curio.dto.BookmarkDTO.CreateBookmarkDto;
import team.backend.curio.dto.BookmarkDTO.BookmarkResponseDto;
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

    // 추가적인 API 엔드포인트는 여기서 작성
}

