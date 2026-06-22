package andrei.chirila.curatedeck.domain.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Arrays;

public enum ElErrorMessage {
    //USER
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    USER_ALREADY_VERIFIED(HttpStatus.CONFLICT, "User already verified"),
    URL_DOES_NOT_MATCH_PATTERN(HttpStatus.BAD_REQUEST, "URL must have a valid domain"),
    //PROJECT
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "Project not found"),
    PROJECT_START_END_DATES_MISMATCH(HttpStatus.BAD_REQUEST, "End date cannot be before start date"),
    //GENERIC
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Something went wrong");

    private final HttpStatus httpStatus;
    private final String description;

    ElErrorMessage(HttpStatus httpStatus, String description) {
        this.httpStatus = httpStatus;
        this.description = description;
    }

    public static HttpStatus getHttpStatus(ElErrorMessage errorMessage) {
        return Arrays.stream(ElErrorMessage.values())
                .filter(e -> e.equals(errorMessage))
                .findFirst()
                .map(e -> e.httpStatus)
                .orElse(null);
    }

    public static String getDescription(ElErrorMessage errorMessage) {
        return Arrays.stream(ElErrorMessage.values())
                .filter(e -> e.equals(errorMessage))
                .findFirst()
                .map(e -> e.description)
                .orElse(null);
    }

    public static ElErrorMessage getFromHttpStatus(HttpStatus code) {
        return Arrays.stream(ElErrorMessage.values())
                .filter(e -> e.httpStatus.equals(code))
                .findFirst()
                .orElse(INTERNAL_SERVER_ERROR);
    }
}
