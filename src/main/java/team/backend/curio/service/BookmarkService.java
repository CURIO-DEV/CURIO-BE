package team.backend.curio.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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

import java.util.ArrayList;
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
    public BookmarkResponseDto createBookmark(CreateBookmarkDto createBookmarkDto, Long userId) {
        // 북마크 생성
        Bookmark bookmark = new Bookmark();
        bookmark.setName(createBookmarkDto.getName());
        bookmark.setColor(createBookmarkDto.getColor());

        // 현재 유저 찾기 : 아이디로
        users currentUser = userRepository.findById(userId)
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
                .filter(user -> !user.getUserId().equals(userId)) // 현재 로그인한 유저 제외하고 저장
                .map(users::getEmail)
                .collect(Collectors.toList());



        return new BookmarkResponseDto(savedBookmark.getId(), savedBookmark.getName(), savedBookmark.getColor(), memberEmails);

    }

    // 북마크 수정
    @Transactional
    public Bookmark updateBookmark(Long bookmarkId, CreateBookmarkDto updateDto, Long userId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));

        bookmark.updateBookmark(updateDto.getName(), updateDto.getColor());

        users currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        for (users member : new ArrayList<>(bookmark.getMembers())) {
            bookmark.removeMember(member); //  양방향 모두에서 제거
        }
        bookmark.addMember(currentUser);

        if (updateDto.getMembers() != null && !updateDto.getMembers().isEmpty()) {
            for (String memberEmail : updateDto.getMembers()) {
                users member = userRepository.findByEmail(memberEmail)
                        .orElseThrow(() -> new RuntimeException("Member with email " + memberEmail + " not found"));
                bookmark.addMember(member);
            }
        }
//북마크 수정
        return bookmark; // Bookmark 객체 반환
    }

    // 북마크에서 해당 사용자만 삭제
    @Transactional
    public void deleteBookmarkForUser(Long bookmarkId, String email) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new RuntimeException("북마크를 찾을 수 없습니다."));

        users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다: " + email));

        // 소속된 사용자만 삭제 가능
        boolean isMember = bookmark.getMembers().stream()
                .anyMatch(member -> member.getEmail().equals(email));

        if (!isMember) {
            throw new AccessDeniedException("본인의 북마크가 아닙니다.");
        }

        bookmark.removeMember(user);

        // 멤버가 아예 없으면 북마크도 제거
        if (bookmark.getMembers().isEmpty()) {
            bookmarkRepository.delete(bookmark);
        } else {
            bookmarkRepository.save(bookmark);
        }
    }

    // 북마크에 뉴스 추가
    public void addNewsToBookmark(Long folderId, Long newsId, users user) {
        Bookmark bookmark = bookmarkRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("북마크가 존재하지 않습니다."));

        if (!bookmark.getMembers().contains(user)) {
            throw new AccessDeniedException("본인의 북마크가 아닙니다.");
        }

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new RuntimeException("뉴스가 존재하지 않습니다."));

        if (bookmark.getNewsList().contains(news)) {
            throw new DuplicateNewsInBookmarkException("이미 북마크에 추가된 뉴스입니다.");
        }

        bookmark.getNewsList().add(news);
        bookmarkRepository.save(bookmark);
    }

    // 북마크에서 뉴스 삭제
    public void removeNewsFromBookmark(Long folderId, Long newsId, users user) {
        Bookmark bookmark = bookmarkRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("북마크가 존재하지 않습니다."));

        if (!bookmark.getMembers().contains(user)) {
            throw new AccessDeniedException("본인의 북마크가 아닙니다.");
        }

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new RuntimeException("뉴스가 존재하지 않습니다."));

        bookmark.getNewsList().remove(news);
        bookmarkRepository.save(bookmark);
    }


    // 북마크 리스트
    public List<News> getNewsListForBookmark(users user, Long folderId) {
        Bookmark bookmark = bookmarkRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("북마크가 존재하지 않습니다."));

        if (!bookmark.getMembers().contains(user)) {
            throw new RuntimeException("본인의 북마크가 아닙니다.");
        }

        return bookmark.getNewsList();
    }

}

