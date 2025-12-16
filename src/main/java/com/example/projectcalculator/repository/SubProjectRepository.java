package com.example.projectcalculator.repository;

import com.example.projectcalculator.model.SubProject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
public class SubProjectRepository {

    private final JdbcTemplate jdbcTemplate;

    public SubProjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Henter alle Subprojekter fra subproject-tabellen.
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
     * Henter subprojects for et givent project_id.
     */
    public List<SubProject> findByProjectId(long projectId) {
        String sql = """
                SELECT id, name, description, deadline, project_id
                FROM subproject
                WHERE project_id = ?
                ORDER BY id
                """;
        return jdbcTemplate.query(sql, new Object[]{projectId}, new SubProjectRowMapper());
    }

    /**
     * Opretter et nyt SubProject til et givet projekt.
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
     * Sletter et SubProject efter id.
     */
    public boolean delete(long id) {
        String sql = "DELETE FROM subproject WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows > 0;
    }

    /**
     * Mapper én række fra subproject-tabellen til et SubProject-objekt.
     * Tasks sættes til null her; kan indlæses separat hvis nødvendigt.
     */
    private static class SubProjectRowMapper implements RowMapper<SubProject> {
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
