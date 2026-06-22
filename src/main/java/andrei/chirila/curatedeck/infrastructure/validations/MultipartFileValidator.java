package andrei.chirila.curatedeck.infrastructure.validations;

import andrei.chirila.curatedeck.domain.validations.ValidFileType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.tika.Tika;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

public class MultipartFileValidator implements ConstraintValidator<ValidFileType, MultipartFile> {
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE);
    private static final Tika TIKA = new Tika();

    private long maxFileSize;
    private String invalidFileTypeMessage;
    private String invalidSizeMessage;

    @Override
    public void initialize(ValidFileType constraintAnnotation) {
        this.maxFileSize = constraintAnnotation.maxFileSize();
        this.invalidFileTypeMessage = constraintAnnotation.invalidFileTypeMessage();
        this.invalidSizeMessage = constraintAnnotation.invalidSizeMessage();
    }

    @Override
    public boolean isValid(MultipartFile image, ConstraintValidatorContext context) {
        if (image == null) {
            return true;
        }
        context.disableDefaultConstraintViolation();

        if (image.getSize() > maxFileSize) {
            return setValidationError(invalidSizeMessage, context);
        }

        try {
            String mimeType = TIKA.detect(image.getBytes());
            if (!ALLOWED_IMAGE_TYPES.contains(mimeType)) {
                return setValidationError(invalidFileTypeMessage, context);
            }
        } catch (IOException ex) {
            return setValidationError("Could not detect file type", context);
        }

        return true;
    }

    private static boolean setValidationError(String message, ConstraintValidatorContext context) {
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }
}
