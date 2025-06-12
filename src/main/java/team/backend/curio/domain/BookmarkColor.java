package team.backend.curio.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BookmarkColor {
    RED, ORANGE, YELLOW, GREEN, BLUE, INDIGO, PURPLE;

    @JsonCreator
    public static BookmarkColor from(String value) {
        return BookmarkColor.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }
}
