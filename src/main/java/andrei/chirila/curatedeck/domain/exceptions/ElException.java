package andrei.chirila.curatedeck.domain.exceptions;

public class ElException extends RuntimeException {
    private final ElErrorMessage errorMessage;

    public ElException(ElErrorMessage errorMessage) {
        super();
        this.errorMessage = errorMessage;
    }

    public ElErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
