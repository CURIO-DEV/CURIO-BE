package team.backend.curio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import team.backend.curio.domain.News;
import team.backend.curio.domain.users;
import team.backend.curio.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.List; // List 추가
import java.util.stream.Collectors;

@Service
public class EmailService {

    private final JavaMailSender emailSender;
    private final UserRepository userRepository;
    private final TrendsService trendsService;

    // 홈페이지 도메인 설정 (이 부분은 실제 URL로 설정)
    @Value("${website.domain.url}")
    private String websiteDomain;

    @Autowired
    public EmailService(JavaMailSender emailSender, UserRepository userRepository, TrendsService trendsService) {
        this.emailSender = emailSender;
        this.userRepository = userRepository;
        this.trendsService= trendsService;
    }

    public void sendNewsletter(String userEmail, List<News> trendingNews) {
        System.out.println("📤 메일 전송 시도 중 → " + userEmail);

        // 이메일 내용 설정
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);  // 수신자 이메일
        message.setSubject("[CURIO] 오늘의 뉴스레터");

        try{
        // 이메일 본문 내용 작성
        StringBuilder content = new StringBuilder("안녕하세요, 큐리오입니다.\n 오늘의 인기 뉴스입니다:) \n\n");
        for (News news : trendingNews) {
            // 뉴스 아이디를 사용하여 전체 URL 생성
            String articleUrl = websiteDomain + "/detail/" + news.getNewsId();
            content.append(news.getTitle())
                    .append("\n")
                    .append(articleUrl) // 웹사이트의 해당 기사 링크
                    .append("\n\n");
        }

        System.out.println("🔎 최종 메일 내용:\n" + content);

        message.setText(content.toString());
        emailSender.send(message);

        System.out.println("✅ 메일 전송 성공: " + userEmail);
    } catch (Exception e) {
            System.out.println("❌ 메일 전송 실패: " + userEmail);
            e.printStackTrace();
        }
    }

    // 자동 발송 스케줄링: 매일 아침 7시
    @Scheduled(cron = "0 0 7 * * *", zone = "Asia/Seoul")
    public void scheduleDailyNewsletter() {
        System.out.println("🕒 자동 뉴스레터 발송 시작");
        System.out.println("서버 현재 시각: " + LocalDateTime.now());
        System.out.println("서버 시간대: " + ZoneId.systemDefault());
        List<News> trendingNews = getTrendingNews(); // 트렌드 뉴스 4개

        List<users> newsletterUsers = userRepository.findByNewsletterStatusAndNewsletterEmailNotNull(1); // 수신 동의자

        for (users user : newsletterUsers) {
            sendNewsletter(user.getNewsletterEmail(), trendingNews);
        }
    }

    // 트렌딩 뉴스 가져오기
    private List<News> getTrendingNews() {
        return trendsService.getPopularArticles()
                .stream()
                .map(News::new) // DTO → News 객체로 변환
                .collect(Collectors.toList());
    }
}