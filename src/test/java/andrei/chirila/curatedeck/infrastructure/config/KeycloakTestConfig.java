package andrei.chirila.curatedeck.infrastructure.config;

import org.keycloak.admin.client.Keycloak;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class KeycloakTestConfig {

    @Bean
    public Keycloak keycloak() {
        return Mockito.mock(Keycloak.class);
    }
}
