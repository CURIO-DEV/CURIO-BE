package team.backend.curio.dto.setting;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CustomSettingRequestDto {
    private String summaryType;
    private String newsletterEmail;
    private boolean receiveNewsletter;
    private List<String> categories;
    private String fontSize;

    public int toPreference() {
        if (summaryType == null) return 2;
        return switch (summaryType.toLowerCase()) {
            case "short" -> 1;
            case "medium" -> 2;
            case "long" -> 3;
            default -> 2;
        };
    }
}