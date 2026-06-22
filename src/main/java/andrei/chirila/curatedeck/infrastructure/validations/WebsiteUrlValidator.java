package andrei.chirila.curatedeck.infrastructure.validations;

import andrei.chirila.curatedeck.domain.exceptions.ElErrorMessage;
import andrei.chirila.curatedeck.domain.validations.ValidWebsiteUrl;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class WebsiteUrlValidator implements ConstraintValidator<ValidWebsiteUrl, String> {
    private static final String PATTERN = "^(?:(?:https?://)?(?:[a-zA-Z0-9\\-]+\\.)+[a-zA-Z0-9\\-]{2,}(?:/\\S*)?)?$";

    @Override
    public void initialize(ValidWebsiteUrl constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        if (!url.matches(PATTERN)) {
            context.buildConstraintViolationWithTemplate(ElErrorMessage.getDescription(ElErrorMessage.URL_DOES_NOT_MATCH_PATTERN)).addConstraintViolation();
            return false;
        }

        return true;
    }
}
