package team.backend.curio.dto;

import lombok.Getter;

@Getter
public class PopularKeywordDto {
    private String keyword;


    public PopularKeywordDto(String keyword) {

        this.keyword = keyword;

    }
}
