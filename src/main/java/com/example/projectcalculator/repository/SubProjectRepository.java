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

    public List<SubProject> listAllSubProjectsByProjectId(long projectId) {
        String sql = """
                SELECT id, project_id, name, description, deadline
                FROM subproject
                WHERE project_id = ?
                ORDER BY id
                """;
        return jdbcTemplate.query(sql, new SubProjectRowMapper(), projectId);
    }

    public SubProject findSubProjectById(long projectId,  long id) {
        String sql = """
                SELECT id, project_id, name, description, deadline
                FROM subproject
                WHERE id = ? AND project_id = ?
                """;
        List<SubProject> results = jdbcTemplate.query(sql, new SubProjectRowMapper(), id, projectId);
        return results.isEmpty() ? null : results.get(0);
    }

    public boolean createSubProject(SubProject subproject) {
        String sql = """
                INSERT INTO subproject (project_id, name, description, deadline)
                VALUES (?, ?, ?, ?)
                """;

        int rows = jdbcTemplate.update(
                sql,
                subproject.getProjectId(),
                subproject.getName(),
                subproject.getDescription(),
                subproject.getDeadline()
        );

        return rows > 0;
    }

    public boolean updateSubProject(SubProject subproject) {
        String sql = """
                UPDATE subproject
                SET name = ?, description = ?, deadline = ?
                WHERE id = ? AND project_id = ?
                """;

        int rows = jdbcTemplate.update(
                sql,
                subproject.getName(),
                subproject.getDescription(),
                subproject.getDeadline(),
                subproject.getId(),
                subproject.getProjectId()
        );

        return rows > 0;
    }

    public boolean deleteSubProject(long projectId, long id) {
        String sql = "DELETE FROM subproject WHERE id = ? AND project_id = ?";
        int rows = jdbcTemplate.update(sql, id, projectId);
        return rows > 0;
    }

    private static class SubProjectRowMapper implements RowMapper<SubProject> {
        @Override
        public SubProject mapRow(ResultSet rs, int rowNum) throws SQLException {
            SubProject subproject = new SubProject();
            subproject.setId(rs.getLong("id"));
            subproject.setProjectId(rs.getLong("project_id"));
            subproject.setName(rs.getString("name"));
            subproject.setDescription(rs.getString("description"));
            LocalDate deadline = rs.getDate("deadline") != null
                    ? rs.getDate("deadline").toLocalDate()
                    : null;
            subproject.setDeadline(deadline);
            return subproject;
        }
    }
}
