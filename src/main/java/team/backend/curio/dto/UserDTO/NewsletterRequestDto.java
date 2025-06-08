package team.backend.curio.dto.UserDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class NewsletterRequestDto {
    @JsonProperty("newsletter-email")  // JSON 키와 매핑
    private String newsletterEmail;
}
