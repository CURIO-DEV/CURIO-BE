package team.backend.curio.dto;  // 패키지 경로 수정


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CustomSettingDto {

    private int summaryPreference; // 요약 선호도 (1=짧음, 2=보통, 3=김)

    public CustomSettingDto(int summaryPreference){
        this.summaryPreference=summaryPreference;
    }
}
