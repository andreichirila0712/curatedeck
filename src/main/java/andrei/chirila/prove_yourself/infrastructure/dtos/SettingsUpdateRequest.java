package andrei.chirila.prove_yourself.infrastructure.dtos;

import jakarta.validation.constraints.NotBlank;

public record SettingsUpdateRequest(
        @NotBlank String language,
        @NotBlank String theme,
        @NotBlank String dateFormat) {
}
