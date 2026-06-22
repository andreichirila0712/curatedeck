package andrei.chirila.curatedeck.domain;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    void save(Project project);

    void deleteProjectByIdForUser(String userId, Long id);

    Optional<Project> findById(Long id);

    Optional<Project> findProjectByIdForUser(String userId, Long id);

    List<Project> findAllProjectsForUser(String userId);

    void deleteAllProjectsForUser(String userId);

    List<Project> findAllPublicProjects();

    List<Project> findAllFeaturedProjectsForUser(String userId);

    List<String> findAllTechnologiesForProject(Long id);
}
