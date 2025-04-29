package team.backend.curio.dto;  // 패키지 경로 수정


import lombok.Getter;
import lombok.Setter;
import java.util.List;


@Getter
@Setter

public class CustomSettingDto {

    private int summaryPreference; // 요약 선호도 (1=짧음, 2=보통, 3=김)
    private String summaryType;    // 요약 타입 (short, medium, long)

    private String newsletterEmail;
    private List<String> categories;     // interest 1~4에 매핑
    private String fontSize;           // 글자 크기 (SMALL, MEDIUM, LARGE)

    // 생성자 수정: summaryPreference 값에 따라 summaryType을 설정
    public CustomSettingDto(int summaryPreference) {
        this.summaryPreference = summaryPreference;
        this.summaryType = mapSummaryType(summaryPreference);  // summaryType을 매핑
    }

    public CustomSettingDto()
    {

    }
    // 오버로드 생성자
    public CustomSettingDto(int summaryPreference, String newsletterEmail, List<String> categories, String fontSize) {
        this.summaryPreference = summaryPreference;
        this.summaryType = mapSummaryType(summaryPreference);
        this.newsletterEmail = newsletterEmail;
        this.categories = categories;
        this.fontSize = fontSize;
    }

    // summaryPreference 값을 받아 summaryType을 반환하는 메서드
    private String mapSummaryType(int summaryPreference) {
        switch (summaryPreference) {
            case 1: return "short";  // 짧음
            case 2: return "medium"; // 보통
            case 3: return "long";   // 길음
            default: return "medium"; // 기본값은 "medium"
        }
    }
}
