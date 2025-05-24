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
    public void recommendNews(Long userId, Long newsId) {
        UserAction userAction = userActionRepository.findByUserIdAndNewsId(userId, newsId)
                .orElse(UserAction.builder()
                        .userId(userId)
                        .newsId(newsId)
                        .build());

        // 추천을 클릭할 때 비추천을 취소
        if (userAction.getVote() == -1) {
            userAction.setVote(0); // 비추천 취소
            userActionRepository.save(userAction);
        }

        if (userAction.getVote() != 1) {
            userAction.setVote(1); //추천
            userActionRepository.save(userAction);
        }
    }


    @Transactional
    public void cancelRecommend(Long userId, Long newsId) {
        userActionRepository.findByUserIdAndNewsId(userId, newsId)
                .ifPresent(userAction -> {
                    if (userAction.getVote() == 1) {
                        userAction.setVote(0); // 추천 취소
                        userActionRepository.save(userAction);
                    }
                });
    }

    // 비추천 등록
    @Transactional
    public void notRecommendNews(Long userId, Long newsId){
        UserAction userAction=userActionRepository.findByUserIdAndNewsId(userId,newsId)
                .orElse(UserAction.builder()
                        .userId(userId)
                        .newsId(newsId)
                        .build());


        // 비추천을 클릭할 때 추천을 취소
        if (userAction.getVote() == 1) {
            userAction.setVote(0); // 추천 취소
            userActionRepository.save(userAction);
        }

        if (userAction.getVote() != -1){
            userAction.setVote(-1);//비추천
            userActionRepository.save(userAction);
        }
    }

    // 비추천 취소
    @Transactional
    public void cancelNotRecommend(Long userId, Long newsId){
        userActionRepository.findByUserIdAndNewsId(userId,newsId)
                .ifPresent(userAction -> {
                    if(userAction.getVote()==-1){
                        userAction.setVote(0); //비추천 취소
                        userActionRepository.save(userAction);
                    }
                });
    }
}
