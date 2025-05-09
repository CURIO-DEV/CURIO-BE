package team.backend.curio.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import team.backend.curio.dto.BookmarkDTO.CreateBookmarkDto;
import team.backend.curio.dto.BookmarkDTO.BookmarkResponseDto;
import team.backend.curio.domain.Bookmark;
import team.backend.curio.domain.News;
import team.backend.curio.domain.users;
import team.backend.curio.repository.BookmarkRepository;
import team.backend.curio.repository.NewsRepository;
import team.backend.curio.repository.UserRepository;

import java.util.List;
import java.util.ArrayList;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookmarkService(BookmarkRepository bookmarkRepository, UserRepository userRepository,NewsRepository newsRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.newsRepository = newsRepository;
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
        bookmark.setNewsList(new ArrayList<>());

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

    @Transactional
    public void deleteBookmark(Long bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));
        bookmarkRepository.delete(bookmark);
    }

    public void addNewsToBookmark(Long folderId, Long newsId) {
        Bookmark bookmark = bookmarkRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new RuntimeException("News not found"));

        bookmark.addNews(news);
        bookmarkRepository.save(bookmark);
    }

    public void removeNewsFromBookmark(Long folderId, Long newsId) {
        Bookmark bookmark = bookmarkRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new RuntimeException("News not found"));

        bookmark.removeNews(news);
        bookmarkRepository.save(bookmark);
    }

    // 북마크 리스트
    public List<Bookmark> getAllBookmarks() {
        return bookmarkRepository.findAll();
    }

    public Bookmark getBookmarkById(Long id) {
        return bookmarkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));
    }

}

