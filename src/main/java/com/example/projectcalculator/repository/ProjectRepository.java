package com.example.projectcalculator.repository;

import com.example.projectcalculator.model.Project;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
public class ProjectRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean create(Project project) {
        String sql = """
                            
                INSERT INTO project (name, description, deadline)
                            VALUES (?, ?, ?)
            """;

        int rows = jdbcTemplate.update(
                sql,
                project.getName(),
                project.getDescription(),
                project.getDeadline()
        );

        return rows > 0;
    }

    /**
     * Henter alle projekter fra project-tabellen.
     */
    public List<Project> findAllProjects() {
        String sql = """
                SELECT id, name, description, deadline
                FROM project
                ORDER BY id
                """;
        return jdbcTemplate.query(sql, new ProjectRowMapper());
    }

    public boolean createProject(Project project) {
        String sql = "INSERT INTO project (name, description, deadline) VALUES (?, ?, ?)";
        int rows = jdbcTemplate.update(
                sql,
                project.getName(),
                project.getDescription(),
                project.getDeadline()
        );
        return rows > 0;
    }

    public boolean deleteProject(long id) {
        String sql = "DELETE FROM project WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows > 0;
    }

    private static class ProjectRowMapper implements RowMapper<Project> {
        @Override
        public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long id = rs.getLong("id");
            String name = rs.getString("name");
            String description = rs.getString("description");
            LocalDate deadline = rs.getDate("deadline") != null
                    ? rs.getDate("deadline").toLocalDate()
                    : null;
            return new Project(id, name, description, deadline);
        }
    }

  public boolean delete(long id) {
        String sql = "DELETE FROM project WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows > 0;
    }
}


/*
 rows = antal rækker påvirket af SQL-operationen.
 rows > 0  → en række blev slettet/opdateret
 rows == 0 → ingen rækker matchede betingelsen (fx ID findes ikke).

 Returner ikke rows > 0? boolean = false
*/

