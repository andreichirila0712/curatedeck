package andrei.chirila.curatedeck.infrastructure.security;

import andrei.chirila.curatedeck.domain.services.ProjectService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

@Component("projectSecurity")
public class ProjectSecurityService {
    private final ProjectService projectService;

    public ProjectSecurityService(ProjectService projectService) {
        this.projectService = projectService;
    }

    public boolean isOwner(OidcUser principal, Long id) {
        return projectService.isOwnerForProject(principal.getSubject(), id);
    }

    public boolean isGuest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication instanceof AnonymousAuthenticationToken;
    }
}
