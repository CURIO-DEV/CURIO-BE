package team.backend.curio.dto.UserDTO;

import java.util.List;

public class UserInterestRequest {
    private List<String> interests;

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }
}
