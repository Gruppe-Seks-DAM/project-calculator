package com.example.projectcalculator.repository;

import com.example.projectcalculator.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
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

            // Note: estimated_hours column might not exist in TASK table
            // We'll check if the column exists
            try {
                task.setEstimatedHours(rs.getDouble("estimated_hours"));
            } catch (SQLException e) {
                // Column doesn't exist, set to null or default
                task.setEstimatedHours(0.0);
            }

            return task;
        }
    };

    /**
     * #178 - Repository: create(Task)
     * Creates a new task in the database
     * Note: We need to check if we should add estimated_hours column to TASK table
     */
    public Task create(Task task) {
        // First, check if the subproject exists
        if (!subProjectExists(task.getSubProjectId())) {
            throw new IllegalArgumentException("Subproject with ID " + task.getSubProjectId() + " does not exist");
        }

        // SQL for insertion - check if estimated_hours column exists in your TASK table
        // If not, you may need to alter the table: ALTER TABLE task ADD COLUMN estimated_hours DOUBLE;
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

        // Get the generated ID and set it on the task
        Long generatedId = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
        task.setId(generatedId);

        return task;
    }

    /**
     * Alternative create method if estimated_hours column doesn't exist in TASK table
     * This would create a task without estimated hours (to match original schema)
     */
    public Task createWithoutEstimatedHours(Task task) {
        if (!subProjectExists(task.getSubProjectId())) {
            throw new IllegalArgumentException("Subproject with ID " + task.getSubProjectId() + " does not exist");
        }

        String sql = """
            INSERT INTO task (subproject_id, name, description, deadline)
            VALUES (?, ?, ?, ?)
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

            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
        task.setId(generatedId);

        return task;
    }

    /**
     * Check if a subproject exists
     */
    private boolean subProjectExists(Long subProjectId) {
        String sql = "SELECT COUNT(*) FROM subproject WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, subProjectId);
        return count != null && count > 0;
    }

    /**
     * Find tasks by subproject ID
     */
    public List<Task> findBySubProjectId(Long subProjectId) {
        String sql = "SELECT * FROM task WHERE subproject_id = ? ORDER BY deadline";
        return jdbcTemplate.query(sql, taskRowMapper, subProjectId);
    }

    /**
     * Find a task by ID
     */
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