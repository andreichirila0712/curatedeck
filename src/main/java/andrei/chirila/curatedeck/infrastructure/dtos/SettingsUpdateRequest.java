package andrei.chirila.curatedeck.infrastructure.dtos;

import jakarta.validation.constraints.NotBlank;

public record SettingsUpdateRequest(
        @NotBlank String language,
        @NotBlank String theme,
        @NotBlank String dateFormat) {
}
