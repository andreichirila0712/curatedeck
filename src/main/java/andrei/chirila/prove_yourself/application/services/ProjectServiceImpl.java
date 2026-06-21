package andrei.chirila.prove_yourself.application.services;

import andrei.chirila.prove_yourself.application.mappers.ProjectMapper;
import andrei.chirila.prove_yourself.domain.Project;
import andrei.chirila.prove_yourself.domain.ProjectRepository;
import andrei.chirila.prove_yourself.domain.User;
import andrei.chirila.prove_yourself.domain.exceptions.ElErrorMessage;
import andrei.chirila.prove_yourself.domain.exceptions.ElException;
import andrei.chirila.prove_yourself.domain.services.ProjectService;
import andrei.chirila.prove_yourself.domain.services.UserService;
import andrei.chirila.prove_yourself.infrastructure.dtos.ProjectCreateRequest;
import andrei.chirila.prove_yourself.infrastructure.dtos.PublicProjectViewDto;
import andrei.chirila.prove_yourself.infrastructure.dtos.ProjectUpdateRequest;
import andrei.chirila.prove_yourself.infrastructure.dtos.PublicProjectDto;
import andrei.chirila.prove_yourself.infrastructure.storage.S3Utility;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectServiceImpl implements ProjectService {
    private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);
    private final ProjectRepository repository;
    private final UserService userService;
    private final S3Utility s3Utility;
    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(ProjectRepository repository, UserService userService, S3Utility s3Utility, ProjectMapper projectMapper) {
        this.repository = repository;
        this.userService = userService;
        this.s3Utility = s3Utility;
        this.projectMapper = projectMapper;
    }

    @Override
    @CacheEvict(value = "projects", key = "#userId")
    public Project addProject(String userId, ProjectCreateRequest data) {
        if (data.startDate() != null && data.endDate() != null) {
            if (data.endDate().isBefore(data.startDate())) {
                throw new ElException(ElErrorMessage.PROJECT_START_END_DATES_MISMATCH);
            }
        }

        User user = this.userService.getUser(userId);

        Project project = new Project();

        project.setRole(data.role());
        project.setTitle(data.title());
        project.setTechnologies(projectMapper.toList(data.technologies()));
        Project.ProjectDescription description = project.getDescription();
        description.setChallenge(data.challenge());
        description.setSolution(data.solution());
        description.setResult(data.result());
        project.setDescription(description);
        project.setLiveUrl(data.live());
        project.setRepositoryUrl(data.repository());
        project.setStartDate(data.startDate());
        project.setEndDate(data.endDate());
        project.setFeatured(false);
        project.setVisible(false);
        project.setUser(user);

        this.repository.save(project);
        logger.info("[PROJECT] : Project with title {} added successfully for user with identifier {}", project.getTitle(), userId);
        return project;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "public_projects", allEntries = true),
            @CacheEvict(value = "featured_projects", key = "#userId"),
            @CacheEvict(value = "technologies", key = "#id"),
            @CacheEvict(value = "projects", key = "#userId")
    })
    public void deleteProject(String userId, Long id) {
        this.repository.deleteProjectByIdForUser(userId, id);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "public_projects", allEntries = true),
            @CacheEvict(value = "technologies", key = "#id"),
            @CacheEvict(value = "featured_projects", key = "#userId"),
            @CacheEvict(value = "projects", key = "#userId"),
            @CacheEvict(value = "public_project", key = "#id")
    })
    public void updateProject(String userId, Long id, ProjectUpdateRequest data) {
        if (data.startDate() != null && data.endDate() != null) {
            if (data.endDate().isBefore(data.startDate())) {
                logger.error("[PROJECT] : End date {} of the project is before its start date {} for update request of project with id {}", data.endDate(), data.startDate(), id);
                throw new ElException(ElErrorMessage.PROJECT_START_END_DATES_MISMATCH);
            }
        }

        Project project = this.repository.findProjectByIdForUser(userId, id).orElseThrow(() -> {
            logger.error("[PROJECT] : Project with identifier {} could not be found", id);
            return new ElException(ElErrorMessage.PROJECT_NOT_FOUND);
        });

        if (data.role() != null && !data.role().isBlank()) project.setRole(data.role());

        if (data.title() != null && !data.title().isBlank()) project.setTitle(data.title());

        if (data.challenge() != null && !data.challenge().isBlank())
            project.getDescription().setChallenge(data.challenge());
        if (data.solution() != null && !data.solution().isBlank())
            project.getDescription().setSolution(data.solution());
        if (data.result() != null && !data.result().isBlank()) project.getDescription().setResult(data.result());

        if (data.technologies() != null && !data.technologies().isBlank())
            project.setTechnologies(projectMapper.toList(data.technologies()));

        if (data.live() != null && !data.live().isBlank()) project.setLiveUrl(data.live());
        if (data.repository() != null && !data.repository().isBlank()) project.setRepositoryUrl(data.repository());

        if (data.startDate() != null && data.endDate() != null) {
            project.setStartDate(data.startDate());
            project.setEndDate(data.endDate());
        } else if (data.startDate() != null) {
            if (project.getEndDate().isBefore(data.startDate())) {
                logger.error("[PROJECT] : Update request with start date {} is after current end date {} for project with identifier {}", data.startDate(), project.getEndDate(), id);
                throw new ElException(ElErrorMessage.PROJECT_START_END_DATES_MISMATCH);
            }
            project.setStartDate(data.startDate());
        } else if (data.endDate() != null) {
            if (project.getStartDate().isAfter(data.endDate())) {
                logger.error("[PROJECT] : Update request with end date {} is before current start date {} for project with identifier {}", data.endDate(), project.getStartDate(), id);
                throw new ElException(ElErrorMessage.PROJECT_START_END_DATES_MISMATCH);
            }
            project.setEndDate(data.endDate());
        }
    }

    @Override
    public Project getProjectForUser(String userId, Long id) {
        return this.repository.findProjectByIdForUser(userId, id).orElseThrow(() -> new ElException(ElErrorMessage.PROJECT_NOT_FOUND));
    }

    @Override
    @Cacheable(value = "projects", key = "#userId")
    public List<Project> listProjectsForUser(String userId) {
        return this.repository.findAllProjectsForUser(userId);
    }

    @Override
    @Transactional
    @CacheEvict(value = "projects", key = "#userId")
    public void uploadThumbnail(String userId, Long id, MultipartFile thumbnail) {
        Project project = this.repository.findProjectByIdForUser(userId, id).orElseThrow(() -> new ElException(ElErrorMessage.PROJECT_NOT_FOUND));

        if (project.getMedia().getThumbnail() != null && !project.getMedia().getThumbnail().isBlank()) {
            s3Utility.deleteFile(project.getMedia().getThumbnail());
        }

        String key = this.s3Utility.uploadFile(thumbnail, userId, "projects/" + id + "/thumbnail");
        project.getMedia().setThumbnail(key);
    }

    @Override
    public String getPresignedUrlForThumbnail(String userId, Long id) {
        Project project = this.repository.findProjectByIdForUser(userId, id).orElseThrow(() -> new ElException(ElErrorMessage.PROJECT_NOT_FOUND));

        if (project.getMedia().getThumbnail() == null || project.getMedia().getThumbnail().isEmpty()) {
            return s3Utility.createPresignedUrl("default/default-thumbnail");
        }

        return s3Utility.createPresignedUrl(project.getMedia().getThumbnail());
    }

    @Override
    @Transactional
    @CacheEvict(value = "projects", key = "#userId")
    public void uploadDiagram(String userId, Long id, MultipartFile diagram) {
        Project project = this.repository.findProjectByIdForUser(userId, id).orElseThrow(() -> new ElException(ElErrorMessage.PROJECT_NOT_FOUND));

        if (project.getMedia().getDiagram() != null && !project.getMedia().getDiagram().isBlank()) {
            s3Utility.deleteFile(project.getMedia().getDiagram());
        }

        String key = s3Utility.uploadFile(diagram, userId, "projects/" + id + "/diagram");
        project.getMedia().setDiagram(key);
    }

    @Override
    public String getPresignedUrlForDiagram(String userId, Long id) {
        Project project = this.repository.findProjectByIdForUser(userId, id).orElseThrow(() -> new ElException(ElErrorMessage.PROJECT_NOT_FOUND));

        if (project.getMedia().getDiagram() == null || project.getMedia().getDiagram().isEmpty()) {
            return "add-some-link/default diagram";
        }

        return s3Utility.createPresignedUrl(project.getMedia().getDiagram());
    }

    @Override
    @Transactional
    @CacheEvict(value = "projects", key = "#userId")
    public void uploadDemo(String userId, Long id, MultipartFile demo) {
        Project project = this.repository.findProjectByIdForUser(userId, id).orElseThrow(() -> new ElException(ElErrorMessage.PROJECT_NOT_FOUND));

        if (project.getMedia().getDemo() != null && !project.getMedia().getDemo().isBlank()) {
            s3Utility.deleteFile(project.getMedia().getDemo());
        }

        String key = s3Utility.uploadFile(demo, userId, "projects/" + id + "/demo");
        project.getMedia().setDemo(key);
    }

    @Override
    public String getPresignedUrlForDemo(String userId, Long id) {
        Project project = this.repository.findProjectByIdForUser(userId, id).orElseThrow(() -> new ElException(ElErrorMessage.PROJECT_NOT_FOUND));

        if (project.getMedia().getDemo() == null || project.getMedia().getDemo().isEmpty()) {
            return "add-some-link/default demo";
        }

        return s3Utility.createPresignedUrl(project.getMedia().getDemo());
    }

    @Override
    @Cacheable(value = "public_project", key = "#id")
    public PublicProjectViewDto getPublicProject(Long id) {
        Project project = this.repository.findById(id).filter(Project::isVisible)
                .orElseThrow(() -> new ElException(ElErrorMessage.PROJECT_NOT_FOUND));

        return projectMapper.toPublicProjectView(project, listTechnologiesForProject(id));
    }

    @Override
    public boolean isOwnerForProject(String userId, Long id) {
        return this.repository.findProjectByIdForUser(userId, id).isPresent();
    }

    @Override
    @Transactional
    @CacheEvict(value = "projects", key = "#userId")
    public void deleteAllProjectsForUser(String userId) {
        this.repository.deleteAllProjectsForUser(userId);
    }

    @Override
    @Cacheable(value = "public_projects", key = "'allPublic'")
    public List<PublicProjectDto> listPublicProjects() {
        List<Project> publicProjects = this.repository.findAllPublicProjects();
        Map<Long, String> technologies = new HashMap<>();
        publicProjects.forEach(p -> technologies.put(p.getId(), listTechnologiesForProject(p.getId())));

        return projectMapper.toPublicProjectsList(this.repository.findAllPublicProjects(), technologies);
    }

    @Override
    @Cacheable(value = "featured_projects", key = "#userId")
    public List<Project> listFeaturedProjectsForUser(String userId) {
        return this.repository.findAllFeaturedProjectsForUser(userId);
    }

    @Override
    @Transactional
    @CacheEvict(value = "featured_projects", key = "#userId")
    public void markProjectAsFeaturedForUser(String userId, Long projectId, boolean featured) {
        Project project = this.repository.findProjectByIdForUser(userId, projectId).orElseThrow(() -> new ElException(ElErrorMessage.PROJECT_NOT_FOUND));
        project.setFeatured(featured);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "public_projects", key = "'allPublic'"),
            @CacheEvict(value = "public_project", key = "#projectId")
    })
    public void changeProjectVisibility(String userId, Long projectId, boolean visibility) {
        Project project = this.repository.findProjectByIdForUser(userId, projectId).orElseThrow(() -> new ElException(ElErrorMessage.PROJECT_NOT_FOUND));
        project.setVisible(visibility);
    }

    @Override
    public String getListOfTechnologiesAsString(List<String> technologies) {
        return projectMapper.toString(technologies);
    }

    @Override
    @Cacheable(value = "technologies", key = "#id")
    public String listTechnologiesForProject(Long id) {
        return projectMapper.toString(this.repository.findAllTechnologiesForProject(id));
    }

    @Override
    @Cacheable(value = "project", key = "#userId")
    public PublicProjectViewDto getProjectForUserAsPublicView(String userId, Long id) {
        Project project = this.repository.findById(id).orElseThrow(() -> new ElException(ElErrorMessage.PROJECT_NOT_FOUND));

        return projectMapper.toPublicProjectView(project, listTechnologiesForProject(id));
    }
}
