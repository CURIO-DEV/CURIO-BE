package team.backend.curio;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EnvLogger implements CommandLineRunner {
    @Value("${spring.datasource.url:NOT SET}")
    private String url;

    @Value("${spring.datasource.username:NOT SET}")
    private String user;

    @Value("${spring.profiles.active:default}")
    private String profile;

    @Value("${jwt.secret-key:NOT FOUND}")
    private String secretKey;

    @Override
    public void run(String... args) {
        System.out.println("🟢 [확인용 로그]");
        System.out.println("🔍 active profile: " + profile);
        System.out.println("🔍 DB URL: " + url);
        System.out.println("🔍 DB USER: " + user);
        System.out.println("🔍 jwt: " + secretKey);
    }
}
