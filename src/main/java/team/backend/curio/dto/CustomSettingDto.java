package team.backend.curio.dto;  // 패키지 경로 수정


import lombok.Getter;
import lombok.Setter;
import java.util.List;


@Getter
@Setter

public class CustomSettingDto {
    private Integer socialType;

    private String summaryType;    // 요약 타입 (short, medium, long)
    private boolean receiveNewsletter;
    private String newsletterEmail;
    private List<String> categories;     // interest 1~4에 매핑
    private String fontSize;           // 글자 크기 (SMALL, MEDIUM, LARGE)

    // 생성자 수정: summaryPreference 값에 따라 summaryType을 설정
    public CustomSettingDto(String summaryType) {
        this.summaryType = "medium";  // summaryType을 매핑
    }


    // 오버로드 생성자
    public CustomSettingDto(String summaryType, String newsletterEmail, boolean receiveNewsletter, List<String> categories, String fontSize,int socialType) {
        this.summaryType = summaryType != null ? summaryType : "medium";  // null 방어
        this.newsletterEmail = newsletterEmail;
        this.receiveNewsletter = receiveNewsletter;
        this.categories = categories;
        this.fontSize = fontSize;
        this.socialType = socialType;
    }

    // summaryPreference 값을 받아 summaryType을 반환하는 메서드
    private int mapSummaryPreference(String summaryType) {
        if (summaryType == null) return 2;
        return switch (summaryType.toLowerCase()) {
            case "short" -> 1;
            case "medium" -> 2;
            case "long" -> 3;
            default -> 2;
        };
    }


}
