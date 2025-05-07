package team.backend.curio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonResponseDto<T> {
    private boolean success;
    private String message;
    private T data;
}
