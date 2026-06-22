package andrei.chirila.curatedeck.infrastructure.dtos;

import andrei.chirila.curatedeck.domain.exceptions.ElErrorMessage;

public record ApiElError(ElErrorMessage code, String message) {
}
