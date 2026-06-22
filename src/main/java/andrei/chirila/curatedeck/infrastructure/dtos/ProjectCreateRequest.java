package andrei.chirila.curatedeck.infrastructure.dtos;

import andrei.chirila.curatedeck.domain.validations.ValidWebsiteUrl;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ProjectCreateRequest(
        @NotBlank @Size(max = 16) String role,
        @NotBlank @Size(max = 36) String title,
        @NotBlank @Size(max = 156) String technologies,
        String challenge,
        String solution,
        String result,
        @NotBlank @ValidWebsiteUrl String live,
        @NotBlank @ValidWebsiteUrl String repository,
        LocalDate startDate,
        LocalDate endDate) {
}
