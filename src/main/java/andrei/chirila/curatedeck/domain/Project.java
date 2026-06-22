package andrei.chirila.curatedeck.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
public  class Project implements ProjectView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title")
    @Size(max = 36)
    private String title;
    @Embedded
    private ProjectDescription description = new ProjectDescription();
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Column(name = "visibility")
    private Boolean visible = false;
    @Column(name = "live_url")
    private String liveUrl;
    @Column(name = "repository_url")
    private String repositoryUrl;
    @Column(name = "role")
    private String role;
    @ElementCollection
    private List<String> technologies = new ArrayList<>();
    @Embedded
    private ProjectImage media = new ProjectImage();
    @Column(name = "featured")
    private Boolean featured = false;

    public Project() {}

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ProjectDescription getDescription() {
        if (this.description == null) {
            this.description = new ProjectDescription();
        }
        return description;
    }

    public void setDescription(ProjectDescription description) {
        this.description.setChallenge(description.getChallenge());
        this.description.setSolution(description.getSolution());
        this.description.setResult(description.getResult());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean isVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getLiveUrl() {
        return liveUrl;
    }

    public void setLiveUrl(String liveUrl) {
        this.liveUrl = liveUrl;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(List<String> technologies) {
        this.technologies = technologies;
    }

    public ProjectImage getMedia() {
        if (this.media == null) {
            this.media = new ProjectImage();
        }
        return media;
    }

    public void setMedia(ProjectImage media) {
        this.media.setThumbnail(media.getThumbnail());
        this.media.setDiagram(media.getDiagram());
        this.media.setDemo(media.getDemo());
    }

    public Boolean isFeatured() {
        return this.featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    @Embeddable
    public static class ProjectDescription {
        @Column(columnDefinition = "TEXT")
        private String challenge;
        @Column(columnDefinition = "TEXT")
        private String solution;
        @Column(columnDefinition = "TEXT")
        private String result;

        public ProjectDescription() {}

        public String getChallenge() {
            return challenge;
        }

        public void setChallenge(String challenge) {
            this.challenge = challenge;
        }

        public String getSolution() {
            return solution;
        }

        public void setSolution(String solution) {
            this.solution = solution;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }

    @Embeddable
    public static class ProjectImage {
        @Column(columnDefinition = "TEXT")
        private String thumbnail;
        @Column(columnDefinition = "TEXT")
        private String diagram;
        @Column(columnDefinition = "TEXT")
        private String demo;

        public ProjectImage() {}

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getDiagram() {
            return diagram;
        }

        public void setDiagram(String diagram) {
            this.diagram = diagram;
        }

        public String getDemo() {
            return demo;
        }

        public void setDemo(String demo) {
            this.demo = demo;
        }
    }
}
