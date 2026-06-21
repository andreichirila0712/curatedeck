package andrei.chirila.prove_yourself.infrastructure.controllers;

import andrei.chirila.prove_yourself.domain.services.ProjectService;
import andrei.chirila.prove_yourself.domain.services.UserService;
import andrei.chirila.prove_yourself.infrastructure.config.ApiConfig;
import andrei.chirila.prove_yourself.infrastructure.config.WebSecurityConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/api")
public class SiteController {
    private final ProjectService projectService;
    private final UserService userService;

    public SiteController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    @GetMapping("/public/welcome")
    public String welcome(Model model) {
        model.addAttribute("login", WebSecurityConfig.LOGIN);
        model.addAttribute("policy", WebSecurityConfig.POLICY);
        model.addAttribute("terms",WebSecurityConfig.TERMS);

        return "site/welcome";
    }

    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal OidcUser principal) {
        model.addAttribute("projects", ApiConfig.API_BASE_PATH + "/user/projects");
        model.addAttribute("profile",    ApiConfig.API_BASE_PATH + "/user/profile");
        model.addAttribute("settings", ApiConfig.API_BASE_PATH + "/user/settings");
        model.addAttribute("name", principal.getAttribute("given_name"));
        model.addAttribute("theme", userService.getUserSettings(principal.getSubject()).theme());
        model.addAttribute("avatar", userService.getUrlToAvatar(principal.getSubject()));

        return "site/home";
    }

    @GetMapping("/public/privacy")
    public String privacy() {
        return "site/privacy";
    }

    @GetMapping("/public/terms")
    public String terms() {
        return "site/terms";
    }

    @GetMapping("/public/projects/project/{id}")
    public String publicProject(@PathVariable Long id, Model model) {
        model.addAttribute("project", projectService.getPublicProject(id));

       return "site/public-project";
    }

    @GetMapping("/public/projects")
    public String publicProjects(@AuthenticationPrincipal OidcUser principal, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("title", "Public projects");
        model.addAttribute("projects", "/api/project/all-public");
        model.addAttribute("name", principal != null ? principal.getAttribute("given_name") : "");
        model.addAttribute("authenticated", principal != null);

        return "site/public-projects";
    }
}
