package com.example.projectcalculator.repository;

import com.example.projectcalculator.model.Task;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class TaskRepository {

    private final JdbcTemplate jdbcTemplate;

    public TaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Task> findById(Long id) {
        String sql = """
                SELECT id, subproject_id, name, description, deadline
                FROM task
                WHERE id = ?
                """;

        List<Task> list = jdbcTemplate.query(sql, new TaskRowMapper(), id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public boolean update(Task task) {
        String sql = """
                UPDATE task
                SET name = ?, description = ?, deadline = ?
                WHERE id = ?
                """;

        int rows = jdbcTemplate.update(
                sql,
                task.getName(),
                task.getDescription(),
                task.getDeadline() != null ? Date.valueOf(task.getDeadline()) : null,
                task.getId()
        );

        return rows > 0;
    }

    // Mapper én række fra task-tabellen til et Task-objekt
    private static class TaskRowMapper implements RowMapper<Task> {
        @Override
        public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long id = rs.getLong("id");
            Long subProjectId = rs.getLong("subproject_id");
            String name = rs.getString("name");
            String description = rs.getString("description");
            Date date = rs.getDate("deadline");
            LocalDate deadline = date != null ? date.toLocalDate() : null;

            return new Task(id, subProjectId, name, description, deadline);
        }
    }
}