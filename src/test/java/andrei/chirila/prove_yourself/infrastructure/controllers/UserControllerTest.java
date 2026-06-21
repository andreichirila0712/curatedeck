package andrei.chirila.prove_yourself.infrastructure.controllers;

import andrei.chirila.prove_yourself.domain.services.ProjectService;
import andrei.chirila.prove_yourself.domain.services.UserService;
import andrei.chirila.prove_yourself.infrastructure.config.WebSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(WebSecurityConfig.class)
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    MockMvc mock;
    @MockitoBean
    UserService userService;
    @MockitoBean
    ProjectService projectService;
    @MockitoBean
    ClientRegistrationRepository clientRegistrationRepository;

    @Test
    void protectedEndpointReturns401() throws  Exception {
        mock.perform(patch("/api/user/update-theme")
                .with(csrf().asHeader())
                .with(csrf().useInvalidToken())).andExpect(status().isUnauthorized());
    }
}
