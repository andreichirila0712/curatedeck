package andrei.chirila.prove_yourself.infrastructure.dtos;

import andrei.chirila.prove_yourself.domain.ProjectView;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record PublicProjectViewDto(
        @NotBlank String title,
        @NotBlank String role,
        String technologies,
        @NotBlank String challenge,
        @NotBlank String solution,
        @NotBlank String result,
        String diagram,
        String demo,
        String liveUrl,
        String repositoryUrl,
        LocalDate startDate,
        LocalDate endDate
) implements ProjectView {
}
