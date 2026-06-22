package andrei.chirila.curatedeck.infrastructure.dtos;

import jakarta.validation.constraints.NotBlank;

public record KeycloakEventRequest(@NotBlank String eventType, @NotBlank String userId, @NotBlank String timestamp) {
}
