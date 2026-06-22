package andrei.chirila.curatedeck.infrastructure.controllers;

import andrei.chirila.curatedeck.domain.services.ProjectService;
import andrei.chirila.curatedeck.domain.services.UserService;
import andrei.chirila.curatedeck.infrastructure.config.WebSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@Import(WebSecurityConfig.class)
public class ProjectControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    ProjectService projectService;
    @MockitoBean
    UserService userService;
    @MockitoBean
    ClientRegistrationRepository clientRegistrationRepository;

    @Test
    void publicEndpointReturns200() throws Exception {
        mockMvc.perform(get("/api/project/all-public")).andExpect(status().isOk());
    }

    @Test
    void protectedEndpointReturns401() throws Exception {
        mockMvc.perform(get("/api/project/1/edit")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/project/add-project")
                .with(csrf().asHeader())
                .with(csrf().useInvalidToken())).andExpect(status().isUnauthorized());
    }
}
