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
import java.util.List;
import java.util.Optional;

/**
 * Repository class for managing {@link Task} entities in the database.
 *
 * <p>This repository provides comprehensive CRUD operations for Task entities
 * using Spring's {@link JdbcTemplate}. It handles the persistence layer for tasks,
 * which are individual work items within {@link com.example.projectcalculator.model.SubProject SubProject}
 * entities.</p>
 *
 * <p><b>Key Features:</b></p>
 * <ul>
 *   <li>Complete CRUD operations with proper error handling</li>
 *   <li>Optional return types for safe null handling</li>
 *   <li>Foreign key validation (tasks cannot be created for non-existent sub-projects)</li>
 *   <li>Generated key retrieval for new task IDs</li>
 *   <li>Null-safe data mapping with default values</li>
 * </ul>
 *
 * <p><b>Example Usage:</b></p>
 * <pre>
 * // Creating a new task
 * Task task = new Task(null, 101L, "Implement Login", "Create login functionality",
 *                      LocalDate.now().plusDays(14), 8.5);
 * Task createdTask = taskRepository.create(task);
 *
 * // Finding a task by ID
 * Optional<Task> foundTask = taskRepository.findById(1L);
 *
 * // Finding all tasks for a sub-project
 * List<Task> tasks = taskRepository.findBySubProjectId(101L);
 *
 * // Updating a task
 * task.setEstimatedHours(10.0);
 * boolean updated = taskRepository.update(task);
 *
 * // Deleting a task
 * boolean deleted = taskRepository.deleteById(1L);
 * </pre>
 *
 * @author Daniel
 * @version 1.0
 * @since 1.0
 * @see Task
 * @see JdbcTemplate
 * @see Repository
 * @see Optional
 */
@Repository
public class TaskRepository {

    /** JDBC template for executing SQL operations against the database. */
    private final JdbcTemplate jdbcTemplate;

    /** RowMapper for converting ResultSet rows into Task objects. */
    private final RowMapper<Task> taskRowMapper;

    /**
     * Constructs a new TaskRepository with the specified JdbcTemplate.
     *
     * <p>Initializes the repository and creates a custom RowMapper for Task entities
     * that handles null values and sets default values when needed.</p>
     *
     * @param jdbcTemplate the JdbcTemplate to use for database operations
     * @throws NullPointerException if {@code jdbcTemplate} is {@code null}
     */
    @Autowired
    public TaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.taskRowMapper = createTaskRowMapper();
    }

    /**
     * Creates a custom RowMapper for Task entities with null-safe mapping.
     *
     * <p>This mapper handles potential null values in the database by:
     * <ul>
     *   <li>Setting {@code null} deadlines when the database value is null</li>
     *   <li>Defaulting estimated hours to 0.0 when the column cannot be retrieved</li>
     * </ul></p>
     *
     * @return a configured RowMapper for Task entities
     */
    private RowMapper<Task> createTaskRowMapper() {
        return (rs, rowNum) -> {
            Task task = new Task();
            task.setId(rs.getLong("id"));
            task.setSubProjectId(rs.getLong("subproject_id"));
            task.setName(rs.getString("name"));
            task.setDescription(rs.getString("description"));

            Date deadline = rs.getDate("deadline");
            if (deadline != null) {
                task.setDeadline(deadline.toLocalDate());
            }

            try {
                task.setEstimatedHours(rs.getDouble("estimated_hours"));
            } catch (SQLException e) {
                task.setEstimatedHours(0.0);
            }

            return task;
        };
    }

    /**
     * Finds a task by its unique identifier.
     *
     * @param id the ID of the task to find
     * @return an {@link Optional} containing the found task, or {@link Optional#empty()}
     *         if no task with the given ID exists
     * @throws org.springframework.dao.DataAccessException if a database access error occurs
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

    /**
     * Updates an existing task in the database.
     *
     * <p>Updates all modifiable fields of the task: name, description, deadline,
     * and estimated hours. The task must have a valid ID set.</p>
     *
     * @param task the task with updated values (must not be {@code null} and must have a valid ID)
     * @return {@code true} if the task was successfully updated (at least one row affected),
     *         {@code false} if no task with the given ID exists
     * @throws NullPointerException if {@code task} is {@code null}
     * @throws IllegalArgumentException if required fields are invalid
     * @throws org.springframework.dao.DataAccessException if a database access error occurs
     */
    public boolean update(Task task) {
        String sql = """
                UPDATE task
                SET name = ?, description = ?, deadline = ?, estimated_hours = ?
                WHERE id = ?
                """;

        int rows = jdbcTemplate.update(
                sql,
                task.getName(),
                task.getDescription(),
                task.getDeadline() != null ? Date.valueOf(task.getDeadline()) : null,
                task.getEstimatedHours() != null ? task.getEstimatedHours() : 0.0,
                task.getId()
        );

        return rows > 0;
    }

    /**
     * Deletes a task by its ID.
     *
     * <p>This method first checks if the task exists, then deletes it if found.
     * This two-step process prevents unnecessary database operations when the
     * task doesn't exist.</p>
     *
     * @param id the ID of the task to delete
     * @return {@code true} if the task was successfully deleted,
     *         {@code false} if no task with the given ID exists
     * @throws org.springframework.dao.DataAccessException if a database access error occurs
     */
    public boolean deleteById(Long id) {
        if (!existsById(id)) {
            return false;
        }

        String sql = "DELETE FROM task WHERE id = ?";
        int rowsDeleted = jdbcTemplate.update(sql, id);
        return rowsDeleted > 0;
    }

    /**
     * Checks if a task exists by its ID.
     *
     * @param id the ID to check
     * @return {@code true} if a task with the given ID exists, {@code false} otherwise
     * @throws org.springframework.dao.DataAccessException if a database access error occurs
     */
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM task WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    /**
     * Retrieves the sub-project ID associated with a specific task.
     *
     * @param taskId the ID of the task
     * @return an {@link Optional} containing the sub-project ID, or {@link Optional#empty()}
     *         if no task with the given ID exists
     * @throws org.springframework.dao.DataAccessException if a database access error occurs
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

    /**
     * Creates a new task in the database with all fields.
     *
     * <p>This method validates that the parent sub-project exists before creating the task.
     * The generated task ID is retrieved from the database and set on the returned task object.</p>
     *
     * @param task the task to create (must not be {@code null})
     * @return the created task with its generated ID set
     * @throws NullPointerException if {@code task} is {@code null}
     * @throws IllegalArgumentException if the parent sub-project doesn't exist
     * @throws org.springframework.dao.DataAccessException if a database access error occurs
     */
    public Task create(Task task) {
        if (!subProjectExists(task.getSubProjectId())) {
            throw new IllegalArgumentException("Subproject with ID " + task.getSubProjectId() + " does not exist");
        }

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

    /**
     * Creates a new task without estimated hours.
     *
     * <p>This method is similar to {@link #create(Task)} but excludes the estimated hours field.
     * Useful when estimated hours are not available at creation time.</p>
     *
     * @param task the task to create (must not be {@code null})
     * @return the created task with its generated ID set
     * @throws NullPointerException if {@code task} is {@code null}
     * @throws IllegalArgumentException if the parent sub-project doesn't exist
     * @throws org.springframework.dao.DataAccessException if a database access error occurs
     *
     * @see #create(Task)
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
     * Checks if a sub-project exists by its ID.
     *
     * <p>This private method is used for validation before creating tasks.</p>
     *
     * @param subProjectId the ID of the sub-project to check
     * @return {@code true} if the sub-project exists, {@code false} otherwise
     * @throws org.springframework.dao.DataAccessException if a database access error occurs
     */
    private boolean subProjectExists(Long subProjectId) {
        String sql = "SELECT COUNT(*) FROM subproject WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, subProjectId);
        return count != null && count > 0;
    }

    /**
     * Finds all tasks associated with a specific sub-project.
     *
     * <p>Tasks are returned in order of their deadline (ascending). Tasks without
     * a deadline are sorted to appear first.</p>
     *
     * @param subProjectId the ID of the sub-project
     * @return a list of tasks belonging to the specified sub-project, ordered by deadline;
     *         returns an empty list if no tasks exist for the sub-project
     * @throws org.springframework.dao.DataAccessException if a database access error occurs
     */
    public List<Task> findBySubProjectId(Long subProjectId) {
        String sql = "SELECT * FROM task WHERE subproject_id = ? ORDER BY deadline";
        return jdbcTemplate.query(sql, taskRowMapper, subProjectId);
    }
}