package team.backend.curio.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import team.backend.curio.domain.QNews;
import team.backend.curio.dto.NewsDTO.SearchNewsResponseDto;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NewsSearchRepositoryImpl implements NewsSearchRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SearchNewsResponseDto> searchByKeywords(List<String> keywords, Pageable pageable) {
        QNews news = QNews.news;

        BooleanBuilder condition = new BooleanBuilder();
        for (String keyword : keywords) {
            condition.and(
                    news.title.like("%"+keyword+"%:")
                            .or(news.content.like("%"+keyword+"%"))
            );
        }

        List<SearchNewsResponseDto> results = queryFactory
                .select(Projections.constructor(
                        SearchNewsResponseDto.class,
                        news.newsId,
                        news.title,
                        news.content,
                        news.imageUrl
                ))
                .from(news)
                .where(condition)
                .orderBy(news.likeCount.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(news.count())
                .from(news)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0L);
    }
}
