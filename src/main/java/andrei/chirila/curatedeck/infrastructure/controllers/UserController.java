package andrei.chirila.curatedeck.infrastructure.controllers;

import andrei.chirila.curatedeck.domain.services.ProjectService;
import andrei.chirila.curatedeck.domain.services.UserService;
import andrei.chirila.curatedeck.domain.validations.ValidFileType;
import andrei.chirila.curatedeck.infrastructure.config.ApiConfig;
import andrei.chirila.curatedeck.infrastructure.config.WebSecurityConfig;
import andrei.chirila.curatedeck.infrastructure.dtos.KeycloakEventRequest;
import andrei.chirila.curatedeck.infrastructure.dtos.SettingsUpdateRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.FragmentsRendering;

import java.security.MessageDigest;
import java.util.Map;

@Controller
@RequestMapping("/api/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Value("${header.secret}")
    private String secret;
    private final UserService service;
    private final ProjectService projectService;

    public UserController(UserService service, ProjectService projectService) {
        this.service = service;
        this.projectService = projectService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody KeycloakEventRequest data, HttpServletRequest request) {
        if (!MessageDigest.isEqual(request.getHeader("X-Webhook-Secret").getBytes(), secret.getBytes())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        logger.info("[USER] : Event type {} received", data.eventType());
        logger.info("[USER] : Proceeding with account creation...");
        this.service.createAccount(data.userId());
        logger.info("[USER] : Account for user with id {} successfully created", data.userId());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody KeycloakEventRequest data, HttpServletRequest request) {
        if (!MessageDigest.isEqual(request.getHeader("X-Webhook-Secret").getBytes(), secret.getBytes())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        logger.info("[USER]: Received {} event", data.eventType());
        logger.info("[USER]: Proceeding with account activation...");
        this.service.activateAccount(data.userId());
        logger.info("[USER]: Account for user with id {} successfully activated", data.userId());

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/update-language")
    public ResponseEntity<?> updateLanguage(@AuthenticationPrincipal OidcUser principal, String language) {
        logger.info("[USER] : Received request to update language, proceeding with updating...");
        this.service.updateLanguage(principal.getSubject(), language);
        logger.info("[USER] : Language updated successfully");

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-theme")
    public ResponseEntity<?> updateTheme(@AuthenticationPrincipal OidcUser principal, String theme) {
        logger.info("[USER] : Received request to update theme, proceeding with updating...");
        logger.info("[USER] : Theme received: {}", theme);
        this.service.updateTheme(principal.getSubject(), theme);
        logger.info("[USER] : Theme updated successfully");
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-date-format")
    public ResponseEntity<?> updateDateFormat(@AuthenticationPrincipal OidcUser principal, String dateFormat) {
        logger.info("[USER] : Received request to update date format, proceeding with updating...");
        this.service.updateDateFormat(principal.getSubject(), dateFormat);
        logger.info("[USER] : Date format updated successfully");

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/change-email")
    public FragmentsRendering changeEmail(@AuthenticationPrincipal OidcUser principal) {
        logger.info("[USER] : Received request to change email, proceeding with the request...");
        this.service.changeEmail(principal.getSubject());
        logger.info("[USER] : Link successfully sent for email change");

        return FragmentsRendering.fragment("fragments/modals :: change-email").build();
    }

    @PutMapping("/change-password")
    public FragmentsRendering changePassword(@AuthenticationPrincipal OidcUser principal) {
        logger.info("[USER] : Received request to change password, proceeding with the request...");
        this.service.changePassword(principal.getSubject());
        logger.info("[USER] : Link successfully sent for password change");

        return FragmentsRendering.fragment("fragments/modals :: change-password").build();
    }

    @PostMapping("/delete-account")
    public FragmentsRendering deleteAccount(@AuthenticationPrincipal OidcUser principal) {
        logger.info("[USER] : Received request to delete account, proceeding with the request...");
        this.service.deleteAccount(principal.getSubject());
        logger.info("[USER] : Link successfully sent for account deletion");

        return FragmentsRendering.
                fragment("fragments/modals :: delete-account", Map.of("welcome", WebSecurityConfig.LANDING_PAGE_URL))
                .build();
    }

    @PostMapping("/delete-account-db")
    public ResponseEntity<?> deleteAccountDb(@RequestBody KeycloakEventRequest data, HttpServletRequest request) {
        if (!MessageDigest.isEqual(request.getHeader("X-Webhook-Secret").getBytes(), secret.getBytes())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        logger.info("[USER]: Event type {} received", data.eventType());
        logger.info("[USER]: Proceeding with account deletion...");
        this.service.deleteAccountDb(data.userId());
        this.projectService.deleteAllProjectsForUser(data.userId());
        logger.info("[USER] : Account for user with id {} successfully deleted", data.userId());

        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-profile")
    public String updateProfile(@AuthenticationPrincipal OidcUser principal) {
        logger.info("[USER] : Received request to update profile personal info, proceeding with updating...");
        this.service.updateKeycloakUserProfile(principal.getSubject());
        logger.info("[USER] : Link successfully sent for profile personal info updating");

        return "fragments/modals :: update-profile-personal-info";
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") @ValidFileType MultipartFile file, @AuthenticationPrincipal OidcUser principal) {
        if (file == null) {
            return ResponseEntity.ok().build();
        }
        logger.info("[USER] : Received request to upload avatar, proceeding with uploading...");
        this.service.uploadAvatar(file, principal.getSubject());
        logger.info("[USER] : Avatar uploaded successfully");

        return ResponseEntity.ok().build();
    }

    @GetMapping("/avatar")
    public String avatar(@AuthenticationPrincipal OidcUser principal, Model model) {
        model.addAttribute("avatar", this.service.getUrlToAvatar(principal.getSubject()));

        return "fragments/avatar";
    }

    @GetMapping("/settings")
    public String settings(Model model, @AuthenticationPrincipal OidcUser principal) {
        model.addAttribute("home", "/api/home");
        model.addAttribute("updateLanguage", ApiConfig.API_BASE_PATH + "/user/update-language");
        model.addAttribute("updateTheme", ApiConfig.API_BASE_PATH + "/user/update-theme");
        model.addAttribute("updateDateFormat", ApiConfig.API_BASE_PATH + "/user/update-date-format");
        model.addAttribute("changeEmail", ApiConfig.API_BASE_PATH + "/user/change-email");
        model.addAttribute("changePassword", ApiConfig.API_BASE_PATH + "/user/change-password");
        model.addAttribute("deleteAccount", ApiConfig.API_BASE_PATH + "/user/delete-account");

        SettingsUpdateRequest settingsUpdateRequest = this.service.getUserSettings(principal.getSubject());
        model.addAttribute("language", settingsUpdateRequest.language());
        model.addAttribute("theme", settingsUpdateRequest.theme());
        model.addAttribute("dateFormat", settingsUpdateRequest.dateFormat());
        model.addAttribute("name", principal.getAttribute("given_name"));
        model.addAttribute("theme", service.getUserSettings(principal.getSubject()).theme());
        model.addAttribute("avatar", service.getUrlToAvatar(principal.getSubject()));

        return "site/settings";
    }

    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal OidcUser principal) {
        model.addAttribute("home", "/api/home");
        model.addAttribute("avatarUpload", "/api/user/upload-avatar");
        model.addAttribute("avatarRefresh", "/api/user/avatar");
        model.addAttribute("name", principal.getAttribute("given_name"));
        model.addAttribute("theme", service.getUserSettings(principal.getSubject()).theme());
        model.addAttribute("avatar", service.getUrlToAvatar(principal.getSubject()));

        return "site/profile";
    }

    @GetMapping("/projects")
    public String projects(@AuthenticationPrincipal OidcUser principal, Model model) {
        model.addAttribute("home", "/api/home");
        model.addAttribute("projects", "/api/project/all");
        model.addAttribute("addProject", "/api/project/add-project");
        model.addAttribute("name", principal.getAttribute("given_name"));
        model.addAttribute("theme", service.getUserSettings(principal.getSubject()).theme());
        model.addAttribute("avatar", service.getUrlToAvatar(principal.getSubject()));

        return "site/projects";
    }
}
