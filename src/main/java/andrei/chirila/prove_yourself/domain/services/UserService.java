package andrei.chirila.prove_yourself.domain.services;

import andrei.chirila.prove_yourself.domain.User;
import andrei.chirila.prove_yourself.infrastructure.dtos.SettingsUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    SettingsUpdateRequest getUserSettings(String id);

    void updateLanguage(String id, String language);

    void updateTheme(String id, String theme);

    void updateDateFormat(String id, String dateFormat);

    void changeEmail(String id);

    void changePassword(String id);

    void deleteAccount(String id);

    void deleteAccountDb(String id);

    void updateKeycloakUserProfile(String id);

    void uploadAvatar(MultipartFile avatar, String id);

    String getUrlToAvatar(String id);

    User getUser(String id);

    void createAccount(String id);

    void activateAccount(String id);
}
