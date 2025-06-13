package team.backend.curio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ActionGetResponse {
    private Long articleId;
    private boolean status;
}
