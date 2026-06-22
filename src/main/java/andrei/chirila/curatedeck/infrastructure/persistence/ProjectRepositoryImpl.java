package andrei.chirila.curatedeck.infrastructure.persistence;

import andrei.chirila.curatedeck.domain.Project;
import andrei.chirila.curatedeck.domain.ProjectRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProjectRepositoryImpl implements ProjectRepository {
    private final PostgresProjectRepository repository;

    public ProjectRepositoryImpl(PostgresProjectRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Project project) {
        this.repository.save(project);
    }

    @Override
    public void deleteProjectByIdForUser(String userId, Long id) {
        this.repository.deleteProjectForUser(userId, id);
    }

    @Override
    public Optional<Project> findById(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public Optional<Project> findProjectByIdForUser(String userId, Long id) {
        return this.repository.findProjectForUser(userId, id);
    }

    @Override
    public List<Project> findAllProjectsForUser(String userId) {
        return this.repository.findAllProjectsForUser(userId);
    }

    @Override
    public void deleteAllProjectsForUser(String userId) {
        this.repository.deleteAllProjectsForUser(userId);
    }

    @Override
    public List<Project> findAllPublicProjects() {
        return this.repository.findAllPublicProjects();
    }

    @Override
    public List<Project> findAllFeaturedProjectsForUser(String userId) {
        return this.repository.findAllFeaturedProjectsForUser(userId);
    }

    @Override
    public List<String> findAllTechnologiesForProject(Long id) {
        return this.repository.findAllTechnologiesForProject(id);
    }
}
