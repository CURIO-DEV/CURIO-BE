package team.backend.curio.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crawler")
public class CrawlerController {

    @Operation(summary = "크롤러 로컬에서 실행 트리거")
    @PostMapping("/start")
    public ResponseEntity<String> startCrawler() {
        try {
            // 예시로 Python 스크립트 실행 (크롤러 스크립트 경로 지정)
            ProcessBuilder processBuilder = new ProcessBuilder("python3", "/path/to/news_clawler_han.py");
            Process process = processBuilder.start();

            // 프로세스 완료 대기 (필요시)
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                return ResponseEntity.ok("크롤러 실행 성공");
            } else {
                return ResponseEntity.status(500).body("크롤러 실행 중 오류 발생, 종료 코드: " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("크롤러 실행 실패: " + e.getMessage());
        }
    }
}
