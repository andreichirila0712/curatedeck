ALTER TABLE projects
    DROP CONSTRAINT fk_projects_on_user;

ALTER TABLE projects
    ADD CONSTRAINT fk_projects_on_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON UPDATE CASCADE;