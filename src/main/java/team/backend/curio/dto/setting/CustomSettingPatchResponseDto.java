package team.backend.curio.dto.setting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class CustomSettingPatchResponseDto {
    private String summaryType;
    private String newsletterEmail;
    private boolean receiveNewsletter;
    private List<String> categories;
    private String fontSize;
}