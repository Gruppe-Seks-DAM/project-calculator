package com.example.projectcalculator.repository;

import com.example.projectcalculator.model.SubTask;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;

@Repository
public class SubTaskRepository {

    private final JdbcTemplate jdbc;

    public SubTaskRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean create(SubTask subtask) {

        String sql = """
            INSERT INTO subtask (task_id, name, description, deadline, estimated_hours)
            VALUES (?, ?, ?, ?, ?)
        """;

        int rows = jdbc.update(
                sql,
                subtask.getTaskId(),
                subtask.getName(),
                subtask.getDescription(),
                subtask.getDeadline() != null ? Date.valueOf(subtask.getDeadline()) : null,
                subtask.getEstimatedHours()
        );

        return rows > 0;
    }
}