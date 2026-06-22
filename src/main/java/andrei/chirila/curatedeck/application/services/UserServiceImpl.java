package andrei.chirila.curatedeck.application.services;

import andrei.chirila.curatedeck.domain.Role;
import andrei.chirila.curatedeck.domain.User;
import andrei.chirila.curatedeck.domain.UserRepository;
import andrei.chirila.curatedeck.domain.exceptions.ElErrorMessage;
import andrei.chirila.curatedeck.domain.exceptions.ElException;
import andrei.chirila.curatedeck.domain.services.UserService;
import andrei.chirila.curatedeck.infrastructure.config.WebSecurityConfig;
import andrei.chirila.curatedeck.infrastructure.dtos.SettingsUpdateRequest;
import andrei.chirila.curatedeck.infrastructure.storage.S3Utility;
import jakarta.transaction.Transactional;
import org.keycloak.admin.client.Keycloak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.client-id}")
    private String clientId;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final S3Utility s3Utility;
    private final Keycloak keycloak;


    public UserServiceImpl(UserRepository userRepository, S3Utility s3Utility, Keycloak keycloak) {
        this.userRepository = userRepository;
        this.s3Utility = s3Utility;
        this.keycloak = keycloak;
    }

    @Override
    @Transactional
    public void createAccount(String id) {
        User user = new User();
        user.setId(id);
        user.setRole(Role.NOT_VERIFIED);
        user.setLanguage("english");
        user.setTheme("light");
        user.setDateFormat("MM/DD/YYYY");
        user.setVerified(false);

        this.userRepository.save(user);
    }

    @Override
    @Transactional
    public void activateAccount(String id) {
        User user = this.userRepository.findById(id).orElseThrow(() -> new ElException(ElErrorMessage.USER_NOT_FOUND));

        if (user.isVerified()) {
            throw new ElException(ElErrorMessage.USER_ALREADY_VERIFIED);
        }
        user.setVerified(true);
        user.setRole(Role.USER);
    }

    @Override
    public SettingsUpdateRequest getUserSettings(String id) {
        User user = this.userRepository.findById(id).orElseThrow(() -> {
            logger.error("[USER] : User with identifier {} not found", id);
            return new ElException(ElErrorMessage.USER_NOT_FOUND);
        });

        return new SettingsUpdateRequest(
                user.getLanguage(),
                user.getTheme(),
                user.getDateFormat());
    }

    @Override
    @Transactional
    public void updateLanguage(String id, String language) {
        User user = this.userRepository.findById(id).orElseThrow(() -> new ElException(ElErrorMessage.USER_NOT_FOUND));
        user.setLanguage(language);
    }

    @Override
    @Transactional
    public void updateTheme(String id, String theme) {
        User user = this.userRepository.findById(id).orElseThrow(() -> new ElException(ElErrorMessage.USER_NOT_FOUND));
        user.setTheme(theme);
    }

    @Override
    @Transactional
    public void updateDateFormat(String id, String dateFormat) {
        User user = this.userRepository.findById(id).orElseThrow(() -> new ElException(ElErrorMessage.USER_NOT_FOUND));
        user.setDateFormat(dateFormat);
    }

    @Override
    public void changeEmail(String keycloakId) {
        keycloak.realm(realm).users().get(keycloakId).executeActionsEmail(clientId, "http://localhost:8081" + WebSecurityConfig.LANDING_PAGE_URL, List.of("UPDATE_EMAIL"));
    }

    @Override
    public void changePassword(String keycloakId) {
        keycloak.realm(realm).users().get(keycloakId).executeActionsEmail(clientId, "http://localhost:8081" + WebSecurityConfig.LANDING_PAGE_URL, List.of("UPDATE_PASSWORD"));

    }

    @Override
    public void deleteAccount(String keycloakId) {
        keycloak.realm(realm).users().get(keycloakId).executeActionsEmail(clientId, "http://localhost:8081" + WebSecurityConfig.LANDING_PAGE_URL, List.of("delete_account"));
    }

    @Override
    @Transactional
    public void deleteAccountDb(String keycloakId) {
        this.userRepository.deleteById(keycloakId);
    }

    @Override
    public void updateKeycloakUserProfile(String keycloakId) {
        this.keycloak.realm(realm).users().get(keycloakId).executeActionsEmail(clientId, "http://localhost:8081" + WebSecurityConfig.LANDING_PAGE_URL, List.of("UPDATE_PROFILE"));
    }

    @Override
    @Transactional
    public void uploadAvatar(MultipartFile avatar, String keycloakId) {
        User user = this.userRepository.findById(keycloakId).orElseThrow(() -> new ElException(ElErrorMessage.USER_NOT_FOUND));

        if (user.getImages().getAvatar() != null && !user.getImages().getAvatar().isBlank()) {
            this.s3Utility.deleteFile(user.getImages().getAvatar());
        }

        String key = this.s3Utility.uploadFile(avatar, keycloakId, "avatar");
        user.getImages().setAvatar(key);
    }

    @Override
    public String getUrlToAvatar(String userId) {
        User user = this.userRepository.findById(userId).orElseThrow(() -> new ElException(ElErrorMessage.USER_NOT_FOUND));
        if (user.getImages().getAvatar() == null || user.getImages().getAvatar().isBlank()) {
            return s3Utility.createPresignedUrl("default/default-avatar.jpg");
        }

        return this.s3Utility.createPresignedUrl(user.getImages().getAvatar());
    }

    @Override
    public User getUser(String keycloakId) {
        return this.userRepository.findById(keycloakId).orElseThrow(() -> new ElException(ElErrorMessage.USER_NOT_FOUND));
    }
}
