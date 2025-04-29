package team.backend.curio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import team.backend.curio.dto.BookmarkDTO.CreateBookmarkDto;
import team.backend.curio.dto.BookmarkDTO.BookmarkResponseDto;
import team.backend.curio.domain.Bookmark;
import team.backend.curio.domain.users;
import team.backend.curio.repository.BookmarkRepository;
import team.backend.curio.repository.UserRepository;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookmarkService(BookmarkRepository bookmarkRepository, UserRepository userRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.userRepository = userRepository;
    }

    // 북마크 생성
    public BookmarkResponseDto createBookmark(CreateBookmarkDto createBookmarkDto) {
        // 사용자 정보 가져오기 (임시로 user_id 1로 설정, 실제로는 로그인된 사용자 정보로 처리)
        users users = userRepository.findById(1L) // 임시로 1L을 사용, 실제 로그인된 사용자로 변경
                .orElseThrow(() -> new RuntimeException("User not found"));

        // CreateBookmarkDto를 사용하여 Bookmark 객체 생성
        Bookmark bookmark = new Bookmark();
        bookmark.setName(createBookmarkDto.getName());
        bookmark.setColor(createBookmarkDto.getColor());
        bookmark.setCollaboratorEmail1(createBookmarkDto.getCollaboratorEmail1());
        bookmark.setCollaboratorEmail2(createBookmarkDto.getCollaboratorEmail2());
        bookmark.setCollaboratorEmail3(createBookmarkDto.getCollaboratorEmail3());
        bookmark.setUsers(users);  // 사용자 설정

        // 북마크 저장
        Bookmark savedBookmark = bookmarkRepository.save(bookmark);

        // 저장된 북마크 정보를 BookmarkResponseDto로 변환해서 반환
        return new BookmarkResponseDto(
                savedBookmark.getId(),
                savedBookmark.getName(),
                savedBookmark.getColor()
        );
    }

    // 북마크 수정
    public Bookmark updateBookmark(Long bookmarkId, CreateBookmarkDto updateDto) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));

        bookmark.updateBookmark(
                updateDto.getName(),
                updateDto.getColor(),
                updateDto.getCollaboratorEmail1(),
                updateDto.getCollaboratorEmail2(),
                updateDto.getCollaboratorEmail3()
        );

        return bookmarkRepository.save(bookmark);
    }

}

