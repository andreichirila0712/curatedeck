package andrei.chirila.curatedeck.infrastructure.persistence;

import andrei.chirila.curatedeck.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;

import java.util.List;
import java.util.Optional;

public interface PostgresProjectRepository extends JpaRepository<Project, Long> {
    @Modifying
    @NativeQuery("DELETE FROM projects WHERE user_id = ?1 AND id = ?2")
    void deleteProjectForUser(String userId, Long id);
    @NativeQuery("SELECT p.* FROM projects p INNER JOIN users u ON p.user_id = u.id WHERE u.id = ?1 AND p.id = ?2")
    Optional<Project> findProjectForUser(String userId, Long id);
    @NativeQuery("SELECT p.* FROM projects p INNER JOIN users u ON p.user_id = u.id WHERE u.id = ?1")
    List<Project> findAllProjectsForUser(String userId);
    @Modifying
    @NativeQuery("DELETE FROM projects WHERE user_id = ?1")
    void deleteAllProjectsForUser(String userId);
    @NativeQuery("SELECT p.* FROM projects p WHERE p.visibility = true")
    List<Project> findAllPublicProjects();
    @NativeQuery("SELECT technologies FROM project_technologies WHERE project_id = ?1")
    List<String> findAllTechnologiesForProject(Long id);
    @NativeQuery("SELECT p.* FROM projects p WHERE p.user_id = ?1 AND p.featured = true")
    List<Project> findAllFeaturedProjectsForUser(String userId);
}
