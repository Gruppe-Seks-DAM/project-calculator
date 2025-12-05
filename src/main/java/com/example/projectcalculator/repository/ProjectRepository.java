package com.example.projectcalculator.repository;

import com.example.projectcalculator.model.Project;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.dao.EmptyResultDataAccessException;
import java.util.Optional;

@Repository
public class ProjectRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
}