package com.example.projectcalculator.repository;

import com.example.projectcalculator.model.Project;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    private final RowMapper<Project> projectRowMapper = (rs, rowNum) -> {
        Project project = new Project();
        project.setId(rs.getLong("id"));
        project.setName(rs.getString("name"));
        project.setDescription(rs.getString("description"));
        project.setDeadline(rs.getDate("deadline") != null ?
                rs.getDate("deadline").toLocalDate() : null);
        return project;
    };

    public Optional<Project> findById(Long id) {
        String sql = "SELECT * FROM project WHERE id = ?"; // lowercase table name
        try {
            Project project = jdbcTemplate.queryForObject(sql, projectRowMapper, id);
            return Optional.ofNullable(project);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean update(Project project) {
        String sql = "UPDATE project SET name = ?, description = ?, deadline = ? WHERE id = ?"; // lowercase
        int affectedRows = jdbcTemplate.update(sql,
                project.getName(),
                project.getDescription(),
                project.getDeadline(),
                project.getId());
        return affectedRows > 0;
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