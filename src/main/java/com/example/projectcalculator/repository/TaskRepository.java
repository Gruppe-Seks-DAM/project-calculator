package com.example.projectcalculator.repository;

import com.example.projectcalculator.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class TaskRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Task> taskRowMapper = new RowMapper<Task>() {
        @Override
        public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
            Task task = new Task();
            task.setId(rs.getLong("id"));
            task.setSubProjectId(rs.getLong("subproject_id"));
            task.setName(rs.getString("name"));
            task.setDescription(rs.getString("description"));

            Date deadline = rs.getDate("deadline");
            if (deadline != null) {
                task.setDeadline(deadline.toLocalDate());
            }

            // Check if estimated_hours column exists
            try {
                task.setEstimatedHours(rs.getDouble("estimated_hours"));
            } catch (SQLException e) {
                task.setEstimatedHours(null);
            }

            return task;
        }
    };

    /**
     * #190 - Repository: deleteById(id)
     * Deletes a task by its ID
     * Note: Database should have CASCADE delete for subtasks or we handle it manually
     */
    public boolean deleteById(Long id) {
        // First, check if the task exists
        if (!existsById(id)) {
            return false;
        }

        // Delete the task (database should handle cascade deletion of subtasks)
        String sql = "DELETE FROM task WHERE id = ?";
        int rowsDeleted = jdbcTemplate.update(sql, id);

        return rowsDeleted > 0;
    }

    /**
     * Check if a task exists by ID
     */
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM task WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    /**
     * Get subproject ID for a task (useful for redirect after deletion)
     */
    public Optional<Long> getSubProjectIdForTask(Long taskId) {
        String sql = "SELECT subproject_id FROM task WHERE id = ?";
        try {
            Long subProjectId = jdbcTemplate.queryForObject(sql, Long.class, taskId);
            return Optional.ofNullable(subProjectId);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Existing methods (from previous implementation)
    public Task create(Task task) {
        // ... existing create method
        String sql = """
            INSERT INTO task (subproject_id, name, description, deadline, estimated_hours)
            VALUES (?, ?, ?, ?, ?)
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, task.getSubProjectId());
            ps.setString(2, task.getName());
            ps.setString(3, task.getDescription());

            if (task.getDeadline() != null) {
                ps.setDate(4, Date.valueOf(task.getDeadline()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            if (task.getEstimatedHours() != null) {
                ps.setDouble(5, task.getEstimatedHours());
            } else {
                ps.setNull(5, Types.DOUBLE);
            }

            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
        task.setId(generatedId);

        return task;
    }

    private boolean subProjectExists(Long subProjectId) {
        String sql = "SELECT COUNT(*) FROM subproject WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, subProjectId);
        return count != null && count > 0;
    }

    public List<Task> findBySubProjectId(Long subProjectId) {
        String sql = "SELECT * FROM task WHERE subproject_id = ? ORDER BY deadline";
        return jdbcTemplate.query(sql, taskRowMapper, subProjectId);
    }

    public Optional<Task> findById(Long id) {
        String sql = "SELECT * FROM task WHERE id = ?";
        try {
            Task task = jdbcTemplate.queryForObject(sql, taskRowMapper, id);
            return Optional.ofNullable(task);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}