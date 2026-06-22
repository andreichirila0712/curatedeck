package andrei.chirila.curatedeck.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class S3Config {
    @Value("${garage.endpoint}")
    private String garageEndpoint;
    @Value("${garage.key}")
    private String accessKey;
    @Value("${garage.secret}")
    private String secretKey;

    @Bean(destroyMethod = "close")
    @Profile("!integration")
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(garageEndpoint))
                .region(Region.of("garage"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

    @Bean(destroyMethod = "close")
    @Profile("!integration")
    public S3Presigner presigner() {
        return S3Presigner.builder()
                .endpointOverride(URI.create(garageEndpoint))
                .region(Region.of("garage"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }
}
