package team.backend.curio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.backend.curio.domain.UserAction;
import team.backend.curio.domain.News;
import team.backend.curio.repository.UserActionRepository;
import team.backend.curio.repository.NewsRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserActionService {

    private final UserActionRepository userActionRepository;
    private final NewsRepository newsRepository;

    // 좋아요 등록
    @Transactional
    public void likeNews(Long userId, Long newsId) {
        UserAction userAction = userActionRepository.findByUserIdAndNewsId(userId, newsId)
                .orElse(UserAction.builder()
                        .userId(userId)
                        .newsId(newsId)
                        .build());

        if (!userAction.isLike()) {
            userAction.setLike(true);
            userActionRepository.save(userAction);

            News news = newsRepository.findById(newsId).orElseThrow();
            news.setLikeCount(news.getLikeCount() + 1);
            newsRepository.save(news);
        }
    }

    // 좋아요 취소
    @Transactional
    public void unlikeNews(Long userId, Long newsId) {
        UserAction userAction = userActionRepository.findByUserIdAndNewsId(userId, newsId)
                .orElseThrow(() -> new RuntimeException("좋아요 기록이 없습니다."));

        if (userAction.isLike()) {
            userAction.setLike(false);
            userActionRepository.save(userAction);

            News news = newsRepository.findById(newsId).orElseThrow();
            news.setLikeCount(news.getLikeCount() - 1);
            newsRepository.save(news);
        }
    }

    // 추천 등록
    @Transactional
    public int recommendNews(Long userId, Long newsId) {
        UserAction userAction = userActionRepository.findByUserIdAndNewsId(userId, newsId)
                .orElse(UserAction.builder()
                        .userId(userId)
                        .newsId(newsId)
                        .build());

        if (userAction.getVote() != 1) {
            userAction.setVote(1); //추천
        } else {
            userAction.setVote(0); //이미 비추천 상태면 추천 해제
        }

        userActionRepository.save(userAction);
        return userAction.getVote();
    }


    // 비추천 등록
    @Transactional
    public int notRecommendNews(Long userId, Long newsId){
        UserAction userAction=userActionRepository.findByUserIdAndNewsId(userId,newsId)
                .orElse(UserAction.builder()
                        .userId(userId)
                        .newsId(newsId)
                        .build());

        if (userAction.getVote() != -1) {
            userAction.setVote(-1);//비추천
        } else {
            userAction.setVote(0); //이미 비추천 상태면 비추천 해제
        }

        userActionRepository.save(userAction);
        return userAction.getVote();
    }
}
