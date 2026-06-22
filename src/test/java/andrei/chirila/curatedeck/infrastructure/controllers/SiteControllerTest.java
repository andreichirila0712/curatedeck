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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SiteController.class)
@Import(WebSecurityConfig.class)
public class SiteControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    UserService userService;
    @MockitoBean
    ProjectService projectService;
    @MockitoBean
    ClientRegistrationRepository clientRegistrationRepository;

   @Test
   void publicEndpointReturns200() throws Exception {
       mockMvc.perform(get("/api/public/welcome")).andExpect(status().isOk());
   }

   @Test
   void protectedEndpointReturns401() throws Exception {
       mockMvc.perform(get("/api/home")).andExpect(status().isUnauthorized());
   }
}
