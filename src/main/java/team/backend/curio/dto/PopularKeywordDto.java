package team.backend.curio.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class PopularKeywordDto {
    private List<String> keyword;


    public PopularKeywordDto(List<String> keyword) {

        this.keyword = keyword;

    }
}
