package andrei.chirila.prove_yourself.integration;

import andrei.chirila.prove_yourself.domain.services.UserService;
import jakarta.servlet.annotation.MultipartConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.Files;
import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MultipartConfig
public class UserControllerIntegrationTest extends BaseIntegrationTest {
    private static final String PATH_TO_FILE = "src/test/resources/static/default-avatar.jpg";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserService userService;

    @Test
    @Sql(scripts = "/scripts/delete-user.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void registrationEndpointCreatesUser() throws Exception {
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Webhook-Secret", System.getenv("HEADER_SECRET"))
                        .content("""
                                {
                                   "eventType": "REGISTER",
                                   "userId": "user",
                                   "timestamp": "%s"
                                }
                                """.formatted(Instant.now())))
                .andExpect(status().isOk());

        var user = userService.getUser("user");
        assertThat(user).isNotNull();
        assertFalse(user.isVerified());
    }

    @Test
    @Sql(scripts = "/scripts/delete-user.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void verifyEndpointVerifiesUser() throws Exception {
        userService.createAccount("user");

        mockMvc.perform(post("/api/user/verify-email")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Webhook-Secret", System.getenv("HEADER_SECRET"))
                .content("""
                        {
                                   "eventType": "VERIFY",
                                   "userId": "user",
                                   "timestamp": "%s"
                        }
                        """.formatted(Instant.now())))
                .andExpect(status().isOk());

        var user = userService.getUser("user");
        assertThat(user).isNotNull();
        assertTrue(user.isVerified());
    }

    @Test
    @Sql(scripts = "/scripts/delete-user.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void uploadAvatarEndpointsSetsAvatarToUser() throws Exception {
        userService.createAccount("user");
        userService.activateAccount("user");

        File file = new File(PATH_TO_FILE);
        MockMultipartFile multipartFile = new MockMultipartFile("file", file.getName(), MediaType.IMAGE_JPEG_VALUE, Files.readAllBytes(file.toPath()));

        mockMvc.perform(multipart("/api/user/upload-avatar")
                        .file(multipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(oidcLogin().idToken(token -> token.tokenValue("token-id"))
                                .userInfoToken(token -> token.claim("sub", "user")))
                        .with(csrf())
                ).andExpect(status().isOk());
    }
}

