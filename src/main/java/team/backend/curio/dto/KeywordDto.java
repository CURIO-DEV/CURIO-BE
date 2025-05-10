package team.backend.curio.dto;

import lombok.*;

@Getter @Setter
public class KeywordDto {
    private String keyword;
    private int weight;

    public KeywordDto(String keyword, int weight) {
        this.keyword = keyword;
        this.weight = weight;
    }

}
