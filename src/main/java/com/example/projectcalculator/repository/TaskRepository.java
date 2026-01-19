package com.example.projectcalculator.repository;

import com.example.projectcalculator.model.Task;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
public class TaskRepository {

    private final JdbcTemplate jdbcTemplate;

    public TaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Task> listAllTasksBySubProjectId(long subProjectId) {
        String sql = """
                SELECT id, subproject_id, name, description, deadline, estimated_hours
                FROM task
                WHERE subproject_id = ?
                ORDER BY id
                """;
        return jdbcTemplate.query(sql, new TaskRowMapper(), subProjectId);
    }

    public Task findTaskById(long subProjectId, long id) {
        String sql = """
                SELECT id, subproject_id, name, description, deadline, estimated_hours
                FROM task
                WHERE id = ? AND subproject_id = ?
                """;
        List<Task> results = jdbcTemplate.query(sql, new TaskRowMapper(), id, subProjectId);
        return results.isEmpty() ? null : results.get(0);
    }

    public boolean createTask(Task task) {
        String sql = """
                INSERT INTO task (subproject_id, name, description, deadline, estimated_hours)
                VALUES (?, ?, ?, ?, ?)
                """;

        int rows = jdbcTemplate.update(
                sql,
                task.getSubProjectId(),
                task.getName(),
                task.getDescription(),
                task.getDeadline(),
                task.getEstimatedHours()
        );

        return rows > 0;
    }

    public boolean updateTask(Task task) {
        String sql = """
                UPDATE task
                SET name = ?, description = ?, deadline = ?, estimated_hours = ?
                WHERE id = ? AND subproject_id = ?
                """;

        int rows = jdbcTemplate.update(
                sql,
                task.getName(),
                task.getDescription(),
                task.getDeadline(),
                task.getEstimatedHours(),
                task.getId(),
                task.getSubProjectId()
        );

        return rows > 0;
    }

    public boolean deleteTask(long subProjectId, long id) {
        String sql = "DELETE FROM task WHERE id = ? AND subproject_id = ?";
        int rows = jdbcTemplate.update(sql, id, subProjectId);
        return rows > 0;
    }

    private static class TaskRowMapper implements RowMapper<Task> {
        @Override
        public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
            Task task = new Task();
            task.setId(rs.getLong("id"));
            task.setSubProjectId(rs.getLong("subproject_id"));
            task.setName(rs.getString("name"));
            task.setDescription(rs.getString("description"));

            LocalDate deadline = rs.getDate("deadline") != null
                    ? rs.getDate("deadline").toLocalDate()
                    : null;
            task.setDeadline(deadline);

            task.setEstimatedHours(rs.getDouble("estimated_hours"));
            return task;
        }
    }
}
