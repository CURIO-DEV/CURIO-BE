package team.backend.curio.dto.UserDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserInterestResponse {
    private List<String> interests;

    public UserInterestResponse(List<String> interests) {
        this.interests = interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }
}
