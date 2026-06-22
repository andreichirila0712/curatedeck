package andrei.chirila.curatedeck.infrastructure.dtos;

import andrei.chirila.curatedeck.domain.validations.ValidWebsiteUrl;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ProjectUpdateRequest(
        @Size(max = 16) String role,
        @Size(max = 36) String title,
        @Size(max = 156) String technologies,
        String challenge,
        String solution,
        String result,
        @ValidWebsiteUrl String live,
        @ValidWebsiteUrl String repository,
        LocalDate startDate,
        LocalDate endDate) {
}
