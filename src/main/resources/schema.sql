CREATE TABLE project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    deadline DATE
);

CREATE TABLE subproject (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    deadline DATE,
    CONSTRAINT fk_subproject_project
                         FOREIGN KEY (project_id) REFERENCES project(id)
                             ON DELETE CASCADE
);

CREATE TABLE task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subproject_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    deadline DATE,
    CONSTRAINT fk_task_subproject
                   FOREIGN KEY (subproject_id) REFERENCES subproject(id)
                       ON DELETE CASCADE
);

CREATE TABLE subtask (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    deadline DATE,
    estimated_hours DOUBLE NOT NULL CHECK (estimated_hours > 0),
    CONSTRAINT fk_subtask_task
        FOREIGN KEY (task_id) REFERENCES task(id)
            ON DELETE CASCADE
);