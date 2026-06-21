package andrei.chirila.prove_yourself.application.services;

import andrei.chirila.prove_yourself.application.mappers.ProjectMapper;
import andrei.chirila.prove_yourself.domain.Project;
import andrei.chirila.prove_yourself.domain.ProjectRepository;
import andrei.chirila.prove_yourself.domain.exceptions.ElException;
import andrei.chirila.prove_yourself.domain.services.ProjectService;
import andrei.chirila.prove_yourself.domain.services.UserService;
import andrei.chirila.prove_yourself.infrastructure.dtos.ProjectCreateRequest;
import andrei.chirila.prove_yourself.infrastructure.dtos.ProjectUpdateRequest;
import andrei.chirila.prove_yourself.infrastructure.storage.S3Utility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {
    private static final String USER_ID = "abc123";

    @Mock
    private ProjectRepository repository;
    @Mock
    private UserService userService;
    @Mock
    private S3Utility s3Utility;
    private final ProjectMapper projectMapper = new ProjectMapper(s3Utility);
    private ProjectService service;

    @Captor
    private ArgumentCaptor<Project> projectCaptor;

    @BeforeEach
    void init() {
        service = new ProjectServiceImpl(repository, userService, s3Utility, projectMapper);
    }

    @Test
    void createsProject() {
        var request = new ProjectCreateRequest("role",
                "title",
                "Java, Spring",
                "Some challenge.",
                "Some solution.",
                "Some result.",
                "https://test.test",
                "https://test.io",
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2026, 6, 1)
                );
        var project = this.service.addProject(USER_ID, request);

        verify(repository).save(projectCaptor.capture());

        assertEquals(request.role(), project.getRole());
        assertEquals(request.title(), project.getTitle());
        assertIterableEquals(List.of("Java", "Spring"), project.getTechnologies());
        assertEquals(request.challenge(), project.getDescription().getChallenge());
        assertEquals(request.solution(), project.getDescription().getSolution());
        assertEquals(request.result(), project.getDescription().getResult());
        assertEquals(request.live(), project.getLiveUrl());
        assertEquals(request.repository(), project.getRepositoryUrl());
        assertEquals(request.startDate(), project.getStartDate());
        assertEquals(request.endDate(), project.getEndDate());
    }

    @Test
    void deletesProject() {
        this.service.deleteProject(USER_ID, 1L);

        verify(repository).deleteProjectByIdForUser(USER_ID, 1L);
    }

    @Test
    void updatesProject() {
        var project = new Project();
        project.setTitle("title");
        project.setRole("role");
        project.setLiveUrl("https://test.test");
        project.setStartDate(LocalDate.of(2025, 12, 1));
        project.setEndDate(LocalDate.of(2026, 6, 1));

        var request = new ProjectUpdateRequest("new role", "new title", null, null, null, null, "https://new-test.test", null, LocalDate.of(2025, 12, 2), LocalDate.of(2026, 6, 2));

        when(repository.findProjectByIdForUser(USER_ID, 1L)).thenReturn(Optional.of(project));

        this.service.updateProject(USER_ID, 1L, request);

        assertEquals(request.role(), project.getRole());
        assertEquals(request.title(), project.getTitle());
        assertEquals(request.live(), project.getLiveUrl());
        assertEquals(request.startDate(), project.getStartDate());
        assertEquals(request.endDate(), project.getEndDate());
    }

    @Test
    void uploadsThumbnail() {
        var project = new Project();

        when(repository.findProjectByIdForUser(USER_ID, 1L)).thenReturn(Optional.of(project));

        MultipartFile multipartFile = new MockMultipartFile("file.png", new byte[]{1, 3, 4, 21});
        when(s3Utility.uploadFile(multipartFile, USER_ID, "projects/" + 1L + "/thumbnail")).thenReturn("some-url");
        this.service.uploadThumbnail(USER_ID, 1L, multipartFile);

        assertEquals("some-url", project.getMedia().getThumbnail());
    }

    @Test
    void returnsDatesMismatch() {
        var request = new ProjectUpdateRequest(null,
                null,
                null,
                null,
                null, null,
                null,
                null,
                LocalDate.now(),
                LocalDate.now().minusMonths(2)
                );
        assertThrows(ElException.class, () -> this.service.updateProject(USER_ID, 1L, request));
    }

}
