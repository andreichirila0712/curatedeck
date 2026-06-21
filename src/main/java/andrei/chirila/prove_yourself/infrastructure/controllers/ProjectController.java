package andrei.chirila.prove_yourself.infrastructure.controllers;

import andrei.chirila.prove_yourself.domain.Project;
import andrei.chirila.prove_yourself.domain.services.ProjectService;
import andrei.chirila.prove_yourself.domain.services.UserService;
import andrei.chirila.prove_yourself.domain.validations.ValidFileType;
import andrei.chirila.prove_yourself.infrastructure.dtos.ProjectCreateRequest;
import andrei.chirila.prove_yourself.infrastructure.dtos.ProjectUpdateRequest;
import andrei.chirila.prove_yourself.infrastructure.dtos.PublicProjectViewDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@Controller
@RequestMapping("/api/project")
@EnableMethodSecurity
public class ProjectController {
    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);
    private final ProjectService service;
    private final UserService userService;

    public ProjectController(ProjectService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @GetMapping("/all")
    public String all(@AuthenticationPrincipal OidcUser principal, Model model) {
        model.addAttribute("items", this.service.listProjectsForUser(principal.getSubject()));
        model.addAttribute("theme", userService.getUserSettings(principal.getSubject()).theme());

        return "fragments/all-projects";
    }

    @GetMapping("/all-featured")
    public String allFeatured(@AuthenticationPrincipal OidcUser principal, Model model) {
        model.addAttribute("projects", this.service.listFeaturedProjectsForUser(principal.getSubject()));

        return "fragments/all-featured-projects";
    }

    @GetMapping("/all-public")
    public String allPublic(Model model) {
        model.addAttribute("projects", this.service.listPublicProjects());

        return "fragments/all-public-projects";
    }

    @GetMapping("/{id}/view")
    public String viewPage(@AuthenticationPrincipal OidcUser principal, @PathVariable Long id, Model model) {
        PublicProjectViewDto project = this.service.getProjectForUserAsPublicView(principal.getSubject(), id);

        model.addAttribute("project", project);
        model.addAttribute("name", principal.getAttribute("given_name"));
        model.addAttribute("theme", userService.getUserSettings(principal.getSubject()).theme());
        model.addAttribute("avatar", userService.getUrlToAvatar(principal.getSubject()));

        return "site/project";
    }

    @GetMapping("/{id}/edit")
    public String editPage(@AuthenticationPrincipal OidcUser principal, @PathVariable Long id, Model model) {
        Project project = this.service.getProjectForUser(principal.getSubject(), id);

        model.addAttribute("project", project);
        model.addAttribute("updateUrl", "/api/project/" + project.getId() + "/update");
        model.addAttribute("technologies", this.service.listTechnologiesForProject(project.getId()));
        model.addAttribute("thumbnailUrl", "/api/project/" + project.getId() + "/upload-thumbnail");
        model.addAttribute("diagramUrl", "/api/project/" + project.getId() + "/upload-diagram");
        model.addAttribute("demoUrl", "/api/project/" + project.getId() + "/upload-demo");
        model.addAttribute("name", principal.getAttribute("given_name"));
        model.addAttribute("theme", userService.getUserSettings(principal.getSubject()).theme());
        model.addAttribute("avatar", userService.getUrlToAvatar(principal.getSubject()));
        model.addAttribute("delete", "/api/project/" + project.getId() + "/delete");
        model.addAttribute("visibility", project.isVisible() == true ? "public" : "private");
        model.addAttribute("visibilityUrl", "/api/project/" + project.getId() + "/update-visibility");

        return "site/edit-project";
    }

    @GetMapping("/{id}/media/thumbnail")
    @ResponseBody
    public String getThumbnailUrl(@AuthenticationPrincipal OidcUser principal, @PathVariable Long id) {
        return this.service.getPresignedUrlForThumbnail(principal.getSubject(), id);
    }

    @GetMapping("/{id}/media/diagram")
    @ResponseBody
    public String getDiagramUrl(@AuthenticationPrincipal OidcUser principal, @PathVariable Long id) {
        return this.service.getPresignedUrlForDiagram(principal.getSubject(), id);
    }

    @GetMapping("/{id}/media/demo")
    @ResponseBody
    public String getDemoUrl(@AuthenticationPrincipal OidcUser principal, @PathVariable Long id) {
        return this.service.getPresignedUrlForDemo(principal.getSubject(), id);
    }

    @PostMapping("/add-project")
    public ResponseEntity<?> createProject(@AuthenticationPrincipal OidcUser principal, ProjectCreateRequest data) {
        logger.info("[PROJECT] : Received request to create project, proceeding with creation...");
        Project project = this.service.addProject(principal.getSubject(), data);
        logger.info("[PROJECT] : Project with id {} created successfully", project.getId());

        return ResponseEntity.created(URI.create("/api/project/" + project.getId())).build();
    }

    @PostMapping("/{id}/upload-thumbnail")
    public ResponseEntity<?> uploadThumbnail(@AuthenticationPrincipal OidcUser principal, @RequestParam("file") @ValidFileType MultipartFile file, @PathVariable Long id) {
        if (file == null) {
            return ResponseEntity.ok().build();
        }

        logger.info("[PROJECT] : Received thumbnail file, proceeding with uploading...");
        this.service.uploadThumbnail(principal.getSubject(), id, file);
        logger.info("[PROJECT] : Thumbnail file successfully uploaded");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/upload-diagram")
    public ResponseEntity<?> uploadDiagram(@AuthenticationPrincipal OidcUser principal, @RequestParam("file") @ValidFileType MultipartFile file, @PathVariable Long id) {
        if (file == null) {
            return ResponseEntity.ok().build();
        }

        logger.info("[PROJECT] : Received diagram file, proceeding with uploading...");
        this.service.uploadDiagram(principal.getSubject(), id, file);
        logger.info("[PROJECT] : Diagram file successfully uploaded");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/upload-demo")
    public ResponseEntity<?> uploadDemo(@AuthenticationPrincipal OidcUser principal, @RequestParam("file") @ValidFileType MultipartFile file, @PathVariable Long id) {
        if (file == null) {
            return ResponseEntity.ok().build();
        }

        logger.info("[PROJECT] : Received demo file, proceeding with uploading...");
        this.service.uploadDemo(principal.getSubject(), id, file);
        logger.info("[PROJECT] : Demo file successfully uploaded");
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@projectSecurity.isOwner(#principal, #id)")
    @PutMapping("/{id}/update")
    public ResponseEntity<?> update(@AuthenticationPrincipal OidcUser principal, @PathVariable Long id, ProjectUpdateRequest data) {
        logger.info("[PROJECT] : Received request to update project, proceeding with updating...");
        this.service.updateProject(principal.getSubject(), id, data);
        logger.info("[PROJECT] : Project with id {} successfully updated", id);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/featured/{featured}")
    public ResponseEntity<?> featured(@AuthenticationPrincipal OidcUser principal, @PathVariable Long id, @PathVariable boolean featured) {
        logger.info("[PROJECT] : Received request to mark project with id {} as {}", id, featured ? "featured" : "not-featured");
        this.service.markProjectAsFeaturedForUser(principal.getSubject(), id, featured);
        logger.info("[PROJECT] : Successfully marked the project {} as {}", id, featured ? "featured" : "not-featured");

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/update-visibility")
    public ResponseEntity<?> updateVisibility(@AuthenticationPrincipal OidcUser principal, @PathVariable Long id, boolean visibility) {
        logger.info("[PROJECT] : Received request to mark project with id {} as {}", id, visibility);
        this.service.changeProjectVisibility(principal.getSubject(), id, visibility);
        logger.info("[PROJECT] : Successfully marked the project {} as {}", id, visibility);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> delete(@AuthenticationPrincipal OidcUser principal, @PathVariable Long id) {
        logger.info("[PROJECT] : Received request to delete project, proceeding with deletion...");
        this.service.deleteProject(principal.getSubject(), id);
        logger.info("[PROJECT] : Project with id {} successfully deleted", id);

        return ResponseEntity.noContent().build();
    }
}