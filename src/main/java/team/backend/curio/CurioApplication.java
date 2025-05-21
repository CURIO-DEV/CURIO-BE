package team.backend.curio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
public class CurioApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurioApplication.class, args);
		System.out.println("🌍 ACTIVE PROFILE = " + System.getProperty("spring.profiles.active"));
		System.out.println("🌍 ENV PROFILE = " + System.getenv("SPRING_PROFILES_ACTIVE"));

	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}


}

