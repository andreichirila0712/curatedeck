package andrei.chirila.curatedeck.integration;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.keycloak.admin.client.Keycloak;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.IOException;
import java.time.Duration;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    static KeycloakContainer keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:26.6.1")
            .withRealmImportFile("/keycloak/test-realm-realm.json")
            .withStartupTimeout(Duration.ofMinutes(7))
            .withReuse(true);


    static LocalStackContainer localStack = new LocalStackContainer(DockerImageName.parse("localstack/localstack-pro:latest")
    ).withServices(LocalStackContainer.Service.S3)
            .withEnv("LOCALSTACK_AUTH_TOKEN", System.getenv("LOCALSTACK_AUTH_TOKEN"))
            .withStartupTimeout(Duration.ofMinutes(7))
            .withCopyFileToContainer(MountableFile.forClasspathResource("/aws/aws-init.sh", 0075), "/etc/localstack/init/ready.d/aws-init.sh");

    static {
        postgres.start();
        keycloak.start();
        localStack.start();
        try {
            localStack.execInContainer("awslocal", "s3api", "wait", "bucket-exists", "--bucket", "important-files");
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return postgres;
    }

    @Bean
    public DynamicPropertyRegistrar registrar() {
        return registry -> {
            registry.add("spring.security.oauth2.client.provider.keycloak.issuer-uri",
                    () -> keycloak.getAuthServerUrl() + "/realms/test-realm");
            registry.add("aws.s3.endpoint", localStack::getEndpoint);
            registry.add("aws.region", localStack::getRegion);
            registry.add("aws.accessKeyId", localStack::getAccessKey);
            registry.add("aws.secretAccessKey", localStack::getSecretKey);
        };
    }


    @Bean
    @Profile("integration")
    S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(localStack.getEndpoint())
                .region(Region.of(localStack.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())
                )).build();
    }

    @Bean
    @Profile("integration")
    S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .endpointOverride(localStack.getEndpoint())
                .region(Region.of(localStack.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())
                ))
                .build();
    }

    @Bean
    @Profile("integration")
    Keycloak keycloakAdminClient() {
        return keycloak.getKeycloakAdminClient();
    }
}
