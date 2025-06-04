package team.backend.curio.exception;

public class DuplicateNewsInBookmarkException extends RuntimeException {
    public DuplicateNewsInBookmarkException(String message) {
        super(message);
    }
}
