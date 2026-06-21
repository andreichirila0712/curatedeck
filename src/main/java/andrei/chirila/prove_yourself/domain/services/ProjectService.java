package andrei.chirila.prove_yourself.domain.services;

import andrei.chirila.prove_yourself.domain.Project;
import andrei.chirila.prove_yourself.infrastructure.dtos.ProjectCreateRequest;
import andrei.chirila.prove_yourself.infrastructure.dtos.ProjectUpdateRequest;
import andrei.chirila.prove_yourself.infrastructure.dtos.PublicProjectDto;
import andrei.chirila.prove_yourself.infrastructure.dtos.PublicProjectViewDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectService {
    Project addProject(String userId, ProjectCreateRequest data);

    void deleteProject(String userId, Long id);

    void deleteAllProjectsForUser(String id);

    void updateProject(String userId, Long id, ProjectUpdateRequest data);

    Project getProjectForUser(String userId, Long id);

    PublicProjectViewDto getPublicProject(Long id);

    PublicProjectViewDto getProjectForUserAsPublicView(String userId, Long id);

    String getPresignedUrlForThumbnail(String userId, Long id);

    String getPresignedUrlForDiagram(String userId, Long id);

    String getPresignedUrlForDemo(String userId, Long id);

    String getListOfTechnologiesAsString(List<String> technologies);

    List<Project> listProjectsForUser(String userId);

    List<PublicProjectDto> listPublicProjects();

    List<Project> listFeaturedProjectsForUser(String userId);

    String listTechnologiesForProject(Long id);

    void uploadThumbnail(String userId, Long id, MultipartFile thumbnail);

    void uploadDiagram(String userId, Long id, MultipartFile diagram);

    void uploadDemo(String userId, Long id, MultipartFile demo);

    void markProjectAsFeaturedForUser(String userId, Long projectId, boolean featured);

    void changeProjectVisibility(String userId, Long projectId, boolean visibility);

    boolean isOwnerForProject(String userId, Long id);
}
