package andrei.chirila.curatedeck.application.mappers;

import andrei.chirila.curatedeck.domain.Project;
import andrei.chirila.curatedeck.domain.exceptions.ElErrorMessage;
import andrei.chirila.curatedeck.domain.exceptions.ElException;
import andrei.chirila.curatedeck.infrastructure.dtos.PublicProjectDto;
import andrei.chirila.curatedeck.infrastructure.dtos.PublicProjectViewDto;
import andrei.chirila.curatedeck.infrastructure.storage.S3Utility;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@Profile("!integration && !test")
public class ProjectMapper {
    private final S3Utility s3Utility;

    public ProjectMapper(S3Utility s3Utility) {
        this.s3Utility = s3Utility;
    }

    public String toString(List<String> technologies) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < technologies.size() - 1; i++) {
            builder.append(technologies.get(i));
            builder.append(",");
        }
        builder.append(technologies.getLast());

        return builder.toString();
    }

    public List<String> toList(String technologies) {
        return Arrays.stream(technologies.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }

    public PublicProjectDto toPublicProject(Project project, Map<Long, String> technologies) {
        if (project == null) {
            throw new ElException(ElErrorMessage.PROJECT_NOT_FOUND);
        }

        String thumbnailUrl = null;
        if (project.getMedia() != null && project.getMedia().getThumbnail() != null) {
            thumbnailUrl = s3Utility.createPresignedUrl(project.getMedia().getThumbnail());
        }

        return new PublicProjectDto(
                project.getId(),
                project.getTitle(),
                project.getRole(),
                technologies.get(project.getId()),
                thumbnailUrl
        );
    }

    public List<PublicProjectDto> toPublicProjectsList(List<Project> projects, Map<Long, String> technologies) {
        return projects.stream()
                .map(p -> this.toPublicProject(p, technologies))
                .toList();
    }

    public PublicProjectViewDto toPublicProjectView(Project project, String technologies) {
        String diagramUrl = null;
        String demoUrl = null;
        if (project.getMedia() != null) {
            if (project.getMedia().getDiagram() != null)
                diagramUrl = s3Utility.createPresignedUrl(project.getMedia().getDiagram());

            if (project.getMedia().getDemo() != null)
                demoUrl = s3Utility.createPresignedUrl(project.getMedia().getDemo());
        }

        return new PublicProjectViewDto(
                project.getTitle(),
                project.getRole(),
                technologies,
                project.getDescription().getChallenge(),
                project.getDescription().getSolution(),
                project.getDescription().getResult(),
                diagramUrl,
                demoUrl,
                project.getLiveUrl(),
                project.getRepositoryUrl(),
                project.getStartDate(),
                project.getEndDate()
        );
    }
}
