package team.backend.curio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ActionPatchResponse {

    private String message;
    private boolean status;
}
