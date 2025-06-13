package team.backend.curio.service;

import jakarta.persistence.EntityNotFoundException;
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

    @Transactional(readOnly = true)
    public boolean isLiked(Long userId, Long articleId) {
        return userActionRepository.findByUserIdAndNewsId(userId, articleId)
                .map(UserAction::isLike)
                .orElse(false);
    }

    // 좋아요 등록 / 취소
    @Transactional
    public int likeNews(Long userId, Long newsId) {
        UserAction userAction = userActionRepository.findByUserIdAndNewsId(userId, newsId)
                .orElse(UserAction.builder()
                        .userId(userId)
                        .newsId(newsId)
                        .like(false)
                        .build());

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new EntityNotFoundException("해당 뉴스가 존재하지 않습니다. id=" + newsId));

        if (userAction.isLike()) {
            userAction.setLike(false);
            userActionRepository.save(userAction);

            news.setLikeCount(news.getLikeCount() - 1);
            newsRepository.save(news);

            return 0; //좋아요 취소
        } else{
            userAction.setLike(true);
            userActionRepository.save(userAction);

            news.setLikeCount(news.getLikeCount()+1);
            newsRepository.save(news);

            return 1;//좋아요 등록
        }
    }

    @Transactional(readOnly = true)
    public int getVote(Long userId, Long articleId) {
        return userActionRepository.findByUserIdAndNewsId(userId, articleId)
                .map(UserAction::getVote)
                .orElse(0);  // 아무 행동 안 했으면 0으로 반환
    }

    // 추천 등록 / 취소
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


    // 비추천 등록 / 취소
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
