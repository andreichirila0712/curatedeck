package andrei.chirila.curatedeck.domain.validations;

import andrei.chirila.curatedeck.infrastructure.validations.MultipartFileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.http.MediaType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = MultipartFileValidator.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFileType {
    String[] allowed() default {
            MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE
    };

    String message() default "Invalid file type";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    long maxFileSize() default 1024 * 1024;
    String invalidFileTypeMessage() default "Invalid image type";
    String invalidSizeMessage() default "Image size is too big";
}
