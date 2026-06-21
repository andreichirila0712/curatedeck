package andrei.chirila.prove_yourself.domain;

import andrei.chirila.prove_yourself.infrastructure.persistence.PostgresProjectRepository;
import andrei.chirila.prove_yourself.infrastructure.persistence.PostgresUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class ProjectRepositoryTest {
    @Autowired
    PostgresProjectRepository projectRepository;
    @Autowired
    PostgresUserRepository userRepository;
    @Autowired
    TestEntityManager entityManager;

    @Test
    void savesAndReturnsProject() {
        var project = new Project();
        var user = new User();
        user.setId("1");
        userRepository.save(user);

        project.setTitle("Title");
        project.setUser(user);

        projectRepository.save(project);
        entityManager.flush();
        entityManager.clear();

        assertThat(project.getId()).isNotNull();
        assertThat(project.getUser()).isNotNull();
        assertThat(projectRepository.findProjectForUser(project.getUser().getId(), project.getId())).isPresent();
        assertEquals("Title", project.getTitle());
        assertEquals("1", project.getUser().getId());
    }

    @Test
    void returnsAllPublicProjects() {
        var projects = List.of(new Project(), new Project(), new Project(), new Project());
        projects.forEach(p -> p.setVisible(true));
        projectRepository.saveAll(projects);
        entityManager.flush();
        entityManager.clear();

        var result = projectRepository.findAllPublicProjects();
        assertThat(result).isNotNull();
        assertEquals(result.size(), projects.size());
        assertIterableEquals(projects.stream().map(Project::getId).toList(), result.stream().map(Project::getId).toList());
    }

    @Test
    void deletesProject() {
        var project = new Project();
        var user = new User();
        user.setId("1");
        userRepository.save(user);
        project.setUser(user);
        projectRepository.save(project);
        entityManager.flush();
        entityManager.clear();

        assertThat(project.getId()).isNotNull();
        assertThat(project.getUser()).isNotNull();
        var id = project.getId();
        projectRepository.deleteProjectForUser(project.getUser().getId(), project.getId());
        var result = projectRepository.findById(id);
        assertThat(result).isNotPresent();
    }

   @Test
   void returnsProjectTechnologies() {
        var project = new Project();
        var technologies = List.of("Java", "Spring", "Keycloak");
        project.setTechnologies(technologies);

        projectRepository.save(project);
        entityManager.flush();
        entityManager.clear();

        assertThat(project.getId()).isNotNull();
        assertIterableEquals(technologies, projectRepository.findAllTechnologiesForProject(project.getId()));
   }

   @Test
   void returnsUserProjects() {
        var user = new User();
        user.setId("1");
        userRepository.save(user);
        var projects = List.of(new Project(), new Project(), new Project());
        projects.forEach(p -> p.setUser(user));
        projectRepository.saveAll(projects);

        var result = projectRepository.findAllProjectsForUser(user.getId());
        assertEquals(projects.size(), result.size());
        assertIterableEquals(projects.stream().map(Project::getId).toList(), result.stream().map(Project::getId).toList());
   }

   @Test
    void deletesUserProjects() {
        var projects = List.of(new Project(), new Project(), new Project());
        var user = new User();
        user.setId("1");
        userRepository.save(user);
        projects.forEach(p -> p.setUser(user));

        projectRepository.saveAll(projects);
        entityManager.flush();
        entityManager.clear();

        projectRepository.deleteAllProjectsForUser(user.getId());

        assertIterableEquals(Collections.EMPTY_LIST, projectRepository.findAllProjectsForUser(user.getId()));
   }


}
