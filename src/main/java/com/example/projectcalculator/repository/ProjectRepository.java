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

    ///  DEPENDENCY INJECTION OF JDBCTEMPLATE
    private final JdbcTemplate jdbcTemplate;

    public ProjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    ///  LISTS ALL EXISTING PROJECTS ORDERED BY ID
    public List<Project> listAllProjects() {
        String sql = """
                SELECT id, name, description, deadline
                FROM project
                ORDER BY id
                """;
        return jdbcTemplate.query(sql, new ProjectRowMapper());
    }
    ///  FETCHES A PROJECT BY ID
    public Project findProjectById(long id) {
        String sql = """
        SELECT id, name, description, deadline
        FROM project
        WHERE id = ?
        """;
        /// Execute query; return the project if found, otherwise return null
        List<Project> results = jdbcTemplate.query(sql, new ProjectRowMapper(), id);
        return results.isEmpty() ? null : results.get(0);
    }

    ///  CREATE A NEW PROJECT
    public boolean createProject(Project project) {
        String sql = "INSERT INTO proje t (name, description, deadline) VALUES (?, ?, ?)";
        int rows = jdbcTemplate.update(
                sql,
                project.getName(),
                project.getDescription(),
                project.getDeadline()
        );
        return rows > 0;
    }
    ///  UPDATE A PROJECT BY ID
    public boolean updateProject(Project project) {
        String sql = """
            UPDATE project
            SET name = ?, description = ?, deadline = ?
            WHERE id = ?
            """;
        int rows = jdbcTemplate.update(
                sql,
                project.getName(),
                project.getDescription(),
                project.getDeadline(),
                project.getId()
        );
        return rows > 0;
    }
    ///  DELETE A PROJECT BY ID
    public boolean deleteProject(long id) {
        String sql = "DELETE FROM project WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows > 0;
    }
    /// ROWMAPPER CLASS CONVERTS DATABASE ROWS INTO PROJECT OBJECTS
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
}

