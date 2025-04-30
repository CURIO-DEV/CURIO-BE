package team.backend.curio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import team.backend.curio.domain.News; // News 도메인 클래스 추가

import java.util.List; // List 추가

@Service
public class EmailService {

    private final JavaMailSender emailSender;
    // 홈페이지 도메인 설정 (이 부분은 실제 URL로 설정)
    @Value("${website.domain.url}")
    private String websiteDomain;

    @Autowired
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendNewsletter(String userEmail, List<News> trendingNews) {
        // 이메일 내용 설정
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);  // 수신자 이메일
        message.setSubject("오늘의 뉴스레터");

        // 이메일 본문 내용 작성
        StringBuilder content = new StringBuilder("안녕하세요, 큐리오입니다.\n 오늘의 인기 뉴스입니다:) \n\n");
        for (News news : trendingNews) {
            // 뉴스 아이디를 사용하여 전체 URL 생성
            String articleUrl = websiteDomain + "/domain/detail/" + news.getNewsId();
            content.append(news.getTitle())
                    .append("\n")
                    .append(articleUrl) // 웹사이트의 해당 기사 링크
                    .append("\n\n");
        }

        message.setText(content.toString());
        emailSender.send(message);
    }
}