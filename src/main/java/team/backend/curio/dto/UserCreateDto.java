package team.backend.curio.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDto {
    private String nickname;
    private String email; // 이메일만 받음
}
