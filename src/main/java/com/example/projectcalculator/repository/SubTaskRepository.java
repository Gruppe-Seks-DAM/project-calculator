package com.example.projectcalculator.repository;

import com.example.projectcalculator.model.SubTask;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
public class SubTaskRepository {

    private final JdbcTemplate jdbcTemplate;

    public SubTaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SubTask> listAllSubTasksByTaskID(long taskId) {
        String sql = """
                SELECT id, task_id, name, description, deadline, estimated_hours
                FROM subtask
                WHERE task_id = ?
                ORDER BY id
                """;
        return jdbcTemplate.query(sql, new SubTaskRowMapper(), taskId);
    }

    public SubTask findSubTaskById(long taskId, long id) {
        String sql = """
                SELECT id, task_id, name, description, deadline, estimated_hours
                FROM subtask
                WHERE id = ? AND task_id = ?
                """;
        List<SubTask> results = jdbcTemplate.query(sql, new SubTaskRowMapper(), id, taskId);
        return results.isEmpty() ? null : results.get(0);
    }

    public boolean createSubTask(SubTask subtask) {
        String sql = """
                INSERT INTO subtask (task_id, name, description, deadline, estimated_hours)
                VALUES (?, ?, ?, ?, ?)
                """;

        int rows = jdbcTemplate.update(
                sql,
                subtask.getTaskId(),
                subtask.getName(),
                subtask.getDescription(),
                subtask.getDeadline(),
                subtask.getEstimatedHours()
        );

        return rows > 0;
    }

    public boolean updateSubTask(SubTask subtask) {
        String sql = """
                UPDATE subtask
                SET name = ?, description = ?, deadline = ?, estimated_hours = ?
                WHERE id = ? AND task_id = ?
                """;

        int rows = jdbcTemplate.update(
                sql,
                subtask.getName(),
                subtask.getDescription(),
                subtask.getDeadline(),
                subtask.getEstimatedHours(),
                subtask.getId(),
                subtask.getTaskId()
        );

        return rows > 0;
    }

    public boolean deleteSubTask(long taskId, long id) {
        String sql = "DELETE FROM subtask WHERE id = ? AND task_id = ?";
        int rows = jdbcTemplate.update(sql, id, taskId);
        return rows > 0;
    }

    private static class SubTaskRowMapper implements RowMapper<SubTask> {
        @Override
        public SubTask mapRow(ResultSet rs, int rowNum) throws SQLException {
            SubTask subtask = new SubTask();
            subtask.setId(rs.getLong("id"));
            subtask.setTaskId(rs.getLong("task_id"));
            subtask.setName(rs.getString("name"));
            subtask.setDescription(rs.getString("description"));

            LocalDate deadline = rs.getDate("deadline") != null
                    ? rs.getDate("deadline").toLocalDate()
                    : null;
            subtask.setDeadline(deadline);

            subtask.setEstimatedHours(rs.getDouble("estimated_hours"));
            return subtask;
        }
    }
}
