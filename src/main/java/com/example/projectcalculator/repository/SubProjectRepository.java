package com.example.projectcalculator.repository;

import com.example.projectcalculator.model.SubProject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository class for managing {@link SubProject} entities in the database.
 *
 * <p>This repository provides CRUD operations for SubProject entities using Spring's
 * {@link JdbcTemplate}. It handles the persistence layer for sub-projects,
 * which are components within larger {@link com.example.projectcalculator.model.Project Project}
 * entities.</p>
 *
 * <p><b>Note:</b> The {@link SubProjectRowMapper} creates SubProject objects with a
 * {@code null} tasks list, as the current implementation doesn't retrieve associated tasks.
 * Consider modifying the query or mapper if task retrieval is required.</p>
 *
 * <p><b>Example Usage:</b></p>
 * <pre>
 * // Creating a sub-project within a project
 * SubProject subProject = new SubProject(null, "UI Design", "Design user interface", LocalDate.now().plusMonths(1), null);
 * boolean created = subProjectRepository.createSubProject(subProject, 1L);
 *
 * // Retrieving all sub-projects
 * List<SubProject> subProjects = subProjectRepository.findAllSubProjects();
 *
 * // Deleting a sub-project
 * boolean deleted = subProjectRepository.delete(5L);
 * </pre>
 *
 * @author Magnus
 * @version 1.0
 * @since 1.0
 * @see SubProject
 * @see com.example.projectcalculator.model.Project
 * @see JdbcTemplate
 * @see Repository
 */
@Repository
public class SubProjectRepository {

    /** JDBC template for executing SQL operations against the database. */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Constructs a new SubProjectRepository with the specified JdbcTemplate.
     *
     * @param jdbcTemplate the JdbcTemplate to use for database operations
     * @throws NullPointerException if {@code jdbcTemplate} is {@code null}
     */
    public SubProjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Retrieves all sub-projects from the database, ordered by their ID.
     *
     * <p>This method queries all columns from the "subproject" table, including
     * the {@code project_id} foreign key, and returns them as a list of SubProject
     * objects.</p>
     *
     * <p><b>Note:</b> The returned SubProject objects will have a {@code null} tasks
     * list, as this method doesn't retrieve associated tasks. The {@code project_id}
     * is retrieved but not used in the SubProject constructor.</p>
     *
     * @return a list of all sub-projects in the database, ordered by ID;
     *         returns an empty list if no sub-projects exist
     * @throws org.springframework.dao.DataAccessException if a database access error occurs
     */
    public List<SubProject> findAllSubProjects() {
        String sql = """
                SELECT id, name, description, deadline, project_id
                FROM subproject
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, new SubProjectRowMapper());
    }

    /**
     * Creates a new sub-project in the database associated with a specific project.
     *
     * <p>This method inserts a new sub-project record into the "subproject" table,
     * linking it to a parent project via the {@code project_id} foreign key.</p>
     *
     * @param subProject the sub-project to create (must not be {@code null})
     * @param projectId the ID of the parent project to associate with this sub-project
     * @return {@code true} if the sub-project was successfully created (at least one row affected),
     *         {@code false} otherwise
     * @throws NullPointerException if {@code subProject} is {@code null}
     * @throws IllegalArgumentException if required fields are invalid
     * @throws org.springframework.dao.DataAccessException if a database access error occurs
     */
    public boolean createSubProject(SubProject subProject, long projectId) {
        String sql = "INSERT INTO subproject (name, description, deadline, project_id) VALUES (?, ?, ?, ?)";
        int rows = jdbcTemplate.update(
                sql,
                subProject.getName(),
                subProject.getDescription(),
                subProject.getDeadline(),
                projectId
        );
        return rows > 0;
    }

    /**
     * Deletes a sub-project from the database by its ID.
     *
     * <p>This method removes a sub-project record from the "subproject" table
     * based on the provided ID. It does not cascade delete associated tasks
     * (check database constraints).</p>
     *
     * @param id the ID of the sub-project to delete
     * @return {@code true} if the sub-project was successfully deleted (at least one row affected),
     *         {@code false} if no sub-project with the given ID exists
     * @throws org.springframework.dao.DataAccessException if a database access error occurs
     */
    public boolean delete(long id) {
        String sql = "DELETE FROM subproject WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows > 0;
    }

    /**
     * RowMapper implementation for converting ResultSet rows into {@link SubProject} objects.
     *
     * <p>This inner class handles the mapping of database result set rows to SubProject
     * entity objects. It extracts values from the ResultSet and constructs a new
     * SubProject instance.</p>
     *
     * <p><b>Note:</b> This mapper currently passes {@code null} for the tasks parameter
     * to the SubProject constructor, and doesn't use the {@code project_id} field.
     * Consider modifying if task retrieval or project association is needed.</p>
     *
     * @see RowMapper
     * @see ResultSet
     */
    private static class SubProjectRowMapper implements RowMapper<SubProject> {

        /**
         * Maps a row of the ResultSet to a SubProject object.
         *
         * <p>Extracts the {@code id}, {@code name}, {@code description}, and {@code deadline}
         * from the ResultSet. The {@code project_id} column is retrieved but not used
         * in the current implementation. A {@code null} value is passed for tasks.</p>
         *
         * @param rs the ResultSet to map (must be positioned on a valid row)
         * @param rowNum the number of the current row (0-indexed)
         * @return a new SubProject instance populated with data from the ResultSet
         * @throws SQLException if a database access error occurs or column values cannot be retrieved
         */
        @Override
        public SubProject mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long id = rs.getLong("id");
            String name = rs.getString("name");
            String description = rs.getString("description");
            LocalDate deadline = rs.getDate("deadline") != null
                    ? rs.getDate("deadline").toLocalDate()
                    : null;

            return new SubProject(id, name, description, deadline, null);
        }
    }
}