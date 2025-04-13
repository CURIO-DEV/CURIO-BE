package team.backend.curio.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class UserResponseDto {
    private Long userId;
    private String nickname;
}
