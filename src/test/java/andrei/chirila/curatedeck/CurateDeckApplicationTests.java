package andrei.chirila.curatedeck;

import andrei.chirila.curatedeck.application.mappers.ProjectMapper;
import andrei.chirila.curatedeck.infrastructure.storage.S3Utility;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class CurateDeckApplicationTests {

    @MockitoBean
    private S3Utility s3Utility;
    @MockitoBean
    private ProjectMapper projectMapper;
    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

	@Test
	void contextLoads() {
	}

}
