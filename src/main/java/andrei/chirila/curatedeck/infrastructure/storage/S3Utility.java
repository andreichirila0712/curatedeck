package andrei.chirila.curatedeck.infrastructure.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;

@Component
@Profile("!integration && !test")
public class S3Utility {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    @Value("${garage.bucket}")
    private String bucket;
    Logger logger = LoggerFactory.getLogger(S3Utility.class);

    public S3Utility(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    public String uploadFile(MultipartFile file, String name, String type) {
        String contentType = file.getContentType();

        assert contentType != null;
        String extension = switch (contentType) {
            case "image/png" -> ".png";
            case "image/jpeg" -> ".jpg";
            default -> ".jpeg";
        };

        String key = "user/" + name + "/" + type + "/" + file.getName() + extension;
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .contentType(contentType)
                    .key(key)
                    .build();

            RequestBody body = RequestBody.fromBytes(file.getBytes());
            s3Client.putObject(request, body);
        } catch (IOException ex) {
            logger.error("Could not access file's content", ex);
        }

        return key;
    }

    public String createPresignedUrl(String objectName) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectName)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(24))
                .getObjectRequest(objectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toExternalForm();
    }

    public void deleteFile(String objectName) {
        s3Client.deleteObject(b -> b.bucket(bucket).key(objectName));
    }

}
