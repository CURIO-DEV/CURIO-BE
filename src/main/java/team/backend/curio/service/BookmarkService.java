package team.backend.curio.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import team.backend.curio.dto.BookmarkDTO.CreateBookmarkDto;
import team.backend.curio.dto.BookmarkDTO.BookmarkResponseDto;
import team.backend.curio.domain.Bookmark;
import team.backend.curio.domain.News;
import team.backend.curio.domain.users;
import team.backend.curio.exception.DuplicateNewsInBookmarkException;
import team.backend.curio.repository.BookmarkRepository;
import team.backend.curio.repository.NewsRepository;
import team.backend.curio.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

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
    public BookmarkResponseDto createBookmark(CreateBookmarkDto createBookmarkDto, String email) {
        // 북마크 생성
        Bookmark bookmark = new Bookmark();
        bookmark.setName(createBookmarkDto.getName());
        bookmark.setColor(createBookmarkDto.getColor());

        // 현재 유저 찾기
        users currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("현재 유저를 찾을 수 없습니다."));

        // 북마크에 현재 유저 추가
        bookmark.getMembers().add(currentUser);

        // members가 null이거나 빈 리스트일 경우, 아무것도 추가하지 않음
        if (createBookmarkDto.getMembers() != null && !createBookmarkDto.getMembers().isEmpty()) {
            for (String memberEmail : createBookmarkDto.getMembers()) {
                users member = userRepository.findByEmail(memberEmail)
                        .orElseThrow(() -> new IllegalArgumentException("이메일 " + memberEmail + "에 해당하는 유저를 찾을 수 없습니다."));

                bookmark.getMembers().add(member);
                member.getBookmarks().add(bookmark);
            }
        }

        Bookmark savedBookmark = bookmarkRepository.save(bookmark);

        for (users member : bookmark.getMembers()) {
            userRepository.save(member);
        }

        List<String> memberEmails = bookmark.getMembers().stream()
                .map(user -> user.getEmail())  // 각 user 객체에서 email 추출
                .collect(Collectors.toList());


        return new BookmarkResponseDto(savedBookmark.getId(), savedBookmark.getName(), savedBookmark.getColor(), memberEmails);

    }

    // 북마크 수정
    @Transactional
    public Bookmark updateBookmark(Long bookmarkId, CreateBookmarkDto updateDto, String email) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));

        bookmark.updateBookmark(updateDto.getName(), updateDto.getColor());

        users currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        bookmark.getMembers().clear();
        bookmark.addMember(currentUser);

        if (updateDto.getMembers() != null && !updateDto.getMembers().isEmpty()) {
            for (String memberEmail : updateDto.getMembers()) {
                users member = userRepository.findByEmail(memberEmail)
                        .orElseThrow(() -> new RuntimeException("Member with email " + memberEmail + " not found"));
                bookmark.addMember(member);
            }
        }

        return bookmarkRepository.save(bookmark); // Bookmark 객체 반환
    }

    // 북마크에서 해당 사용자만 삭제
    @Transactional
    public void deleteBookmarkForUser(Long bookmarkId, String email) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new RuntimeException("북마크를 찾을 수 없습니다."));

        users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다 : " +email ));

        if (!bookmark.getMembers().contains(users)) {
            throw new RuntimeException("User is not a member of this bookmark");
        }

        bookmark.removeMember(users);

        // 멤버가 없으면 북마크 삭제
        if (bookmark.getMembers().isEmpty()) {
            bookmarkRepository.delete(bookmark);
        } else {
            bookmarkRepository.save(bookmark);
        }
    }

    // 북마크에 뉴스 추가
    public void addNewsToBookmark(Long folderId, Long newsId) {
        Bookmark bookmark = bookmarkRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new RuntimeException("News not found"));

        // 이미 추가된 뉴스인지 확인
        if (bookmark.getNewsList().contains(news)) {
            throw new DuplicateNewsInBookmarkException("이미 추가된 뉴스입니다.");
        }

        bookmark.addNews(news);

        // 저장 수 증가
        news.setSaveCount(news.getSaveCount() + 1);

        // 저장
        bookmarkRepository.save(bookmark);
        newsRepository.save(news);
    }

    // 북마크에서 뉴스 삭제
    public void removeNewsFromBookmark(Long folderId, Long newsId) {
        Bookmark bookmark = bookmarkRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new RuntimeException("News not found"));

        bookmark.removeNews(news);

        // 저장 수 감소 (0 이하로는 내려가지 않게 처리)
        int currentSaveCount = news.getSaveCount();
        if (currentSaveCount > 0) {
            news.setSaveCount(currentSaveCount - 1);
        }

        // 저장
        bookmarkRepository.save(bookmark);
        newsRepository.save(news);
    }


    // 북마크 리스트
    public List<Bookmark> getAllBookmarks(String email) {
        users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        return bookmarkRepository.findAllByMembersContaining(users);    }

    public Bookmark getBookmarkById(Long id) {
        return bookmarkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));
    }

}

