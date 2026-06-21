package andrei.chirila.prove_yourself.infrastructure.dtos;

import jakarta.validation.constraints.NotBlank;

public record PublicProjectDto(
        @NotBlank Long id,
        @NotBlank String title,
        @NotBlank String role,
        @NotBlank String technologies,
        String thumbnail
) {
}
