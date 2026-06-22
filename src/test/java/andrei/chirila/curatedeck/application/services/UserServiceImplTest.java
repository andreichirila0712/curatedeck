package andrei.chirila.curatedeck.application.services;

import andrei.chirila.curatedeck.domain.Role;
import andrei.chirila.curatedeck.domain.User;
import andrei.chirila.curatedeck.domain.UserRepository;
import andrei.chirila.curatedeck.domain.exceptions.ElException;
import andrei.chirila.curatedeck.domain.services.UserService;
import andrei.chirila.curatedeck.infrastructure.dtos.SettingsUpdateRequest;
import andrei.chirila.curatedeck.infrastructure.storage.S3Utility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    private static final String USER_ID = "abc123";
    @Mock
    private UserRepository repository;
    @Mock
    private S3Utility s3Utility;
    private UserService service;
    @Mock
    private Keycloak keycloak;
    @Mock
    private RealmResource realmResource;
    @Mock
    private UsersResource usersResource;
    @Mock
    private UserResource userResource;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void init() {
        service = new UserServiceImpl(repository, s3Utility, keycloak);
    }

    @Test
    void createsUser() {
        service.createAccount(USER_ID);
        verify(repository).save(userCaptor.capture());

        assertEquals(USER_ID, userCaptor.getValue().getId());
        assertEquals(Role.NOT_VERIFIED, userCaptor.getValue().getRole());
        assertEquals("english", userCaptor.getValue().getLanguage());
        assertEquals("light", userCaptor.getValue().getTheme());
        assertEquals("MM/DD/YYYY", userCaptor.getValue().getDateFormat());
        assertFalse(userCaptor.getValue().isVerified());
    }

    @Test
    void verifiesUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setRole(Role.NOT_VERIFIED);
        user.setVerified(false);

        when(repository.findById(USER_ID)).thenReturn(Optional.of(user));
        service.activateAccount(USER_ID);

        assertEquals(Role.USER, user.getRole());
        assertTrue(user.isVerified());
    }

    @Test
    void returnsUserSettings() {
        User user = new User();
        user.setId(USER_ID);
        user.setLanguage("english");
        user.setTheme("light");
        user.setDateFormat("DD/MM/YYYY");

        when(repository.findById(USER_ID)).thenReturn(Optional.of(user));
        SettingsUpdateRequest result = service.getUserSettings(USER_ID);

        verify(repository).findById(USER_ID);
        assertEquals(user.getLanguage(), result.language());
        assertEquals(user.getTheme(), result.theme());
        assertEquals(user.getDateFormat(), result.dateFormat());
    }

    @Test
    void returnsUserNotFound() {
        assertThrows(ElException.class, () -> service.getUserSettings(USER_ID));
    }

    @Test
    void updatesLanguage() {
        User user = new User();
        user.setId(USER_ID);
        user.setLanguage("english");

        when(repository.findById(USER_ID)).thenReturn(Optional.of(user));
        service.updateLanguage(USER_ID, "spanish");

        assertEquals("spanish", user.getLanguage());
    }

    @Test
    void updatesTheme() {
        User user = new User();
        user.setTheme("light");
        user.setId(USER_ID);

        when(repository.findById(USER_ID)).thenReturn(Optional.of(user));
        service.updateTheme(USER_ID, "dark");

        assertEquals("dark", user.getTheme());
    }

    @Test
    void updatesFormat() {
        User user = new User();
        user.setId(USER_ID);
        user.setDateFormat("DD/MM/YYYY");

        when(repository.findById(USER_ID)).thenReturn(Optional.of(user));
        service.updateDateFormat(USER_ID,"MM/DD/YYYY");

        assertEquals("MM/DD/YYYY", user.getDateFormat());
    }

    @Test
    void deletesAccount() {
        service.deleteAccountDb(USER_ID);

        verify(repository).deleteById(USER_ID);
    }

    @Test
    void uploadsAvatar() {
        MockMultipartFile file = new MockMultipartFile("file", "file", MediaType.IMAGE_PNG_VALUE, new byte[] {1, 2, 3, 4});
        User user = new User();
        user.setId(USER_ID);
        when(repository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(s3Utility.uploadFile(file, USER_ID, "avatar")).thenReturn("user/" + USER_ID + "/avatar/" + file.getName() + ".png");


        service.uploadAvatar(file, USER_ID);

        assertEquals("user/" + USER_ID + "/avatar/" + file.getName() + ".png", user.getImages().getAvatar());
    }

    @Test
    void returnsUrlForDefaultAvatar() {
        User user = new User();
        user.setId(USER_ID);
        user.getImages().setAvatar(null);
        when(repository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(s3Utility.createPresignedUrl("default/default-avatar.jpg")).thenReturn("https://default-presigned-url");

        String result = this.service.getUrlToAvatar(USER_ID);

        assertEquals("https://default-presigned-url", result);
    }

    @Test
    void returnsUrlToAvatar() {
        User user = new User();
        user.setId(USER_ID);
        user.getImages().setAvatar("/user/" + USER_ID + "/avatar/file.png");
        when(repository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(s3Utility.createPresignedUrl(user.getImages().getAvatar())).thenReturn("https://some-presigned-url");

        String result = this.service.getUrlToAvatar(USER_ID);

        assertEquals("https://some-presigned-url", result);
    }

    @Test
    void shouldSendEmailToChangePassword() {
        when(keycloak.realm(any())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(anyString())).thenReturn(userResource);
        service.changePassword(anyString());

        verify(userResource).executeActionsEmail(any(), anyString(), eq(List.of("UPDATE_PASSWORD")));
    }

    @Test
    void sendsEmailForPasswordChange() {
        when(keycloak.realm(any())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(anyString())).thenReturn(userResource);
        service.changeEmail(anyString());

        verify(userResource).executeActionsEmail(any(), anyString(), eq(List.of("UPDATE_EMAIL")));
    }

    @Test
    void sendsEmailForAccountDeletion() {
        when(keycloak.realm(any())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(anyString())).thenReturn(userResource);
        service.deleteAccount(anyString());

        verify(userResource).executeActionsEmail(any(), anyString(), eq(List.of("delete_account")));
    }

    @Test
    void sendsEmailForAccountInfoUpdate() {
        when(keycloak.realm(any())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(anyString())).thenReturn(userResource);
        service.updateKeycloakUserProfile(anyString());

        verify(userResource).executeActionsEmail(any(), anyString(), eq(List.of("UPDATE_PROFILE")));
    }
}
