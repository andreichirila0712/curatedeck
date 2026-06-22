ALTER TABLE project_technologies
    DROP CONSTRAINT fk_project_technologies_on_project;

ALTER TABLE project_technologies
    ADD CONSTRAINT fk_project_technologies_on_project
        FOREIGN KEY (project_id)
            REFERENCES projects(id)
            ON UPDATE CASCADE;

