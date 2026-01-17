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

    public List<SubProject> listAllSubProjects() {
        String sql = """
                SELECT id, project_id, name, description, deadline
                FROM subproject
                ORDER BY id
                """;
        return jdbcTemplate.query(sql, new SubProjectRowMapper());
    }

    public SubProject findSubProjectById(long id) {
        String sql = """
                SELECT id, project_id, name, description, deadline
                FROM subproject
                WHERE id = ?
                """;
        List<SubProject> results = jdbcTemplate.query(sql, new SubProjectRowMapper(), id);
        return results.isEmpty() ? null : results.get(0);
    }

    public boolean createSubProject(SubProject subproject) {
        String sql = "INSERT INTO subproject (project_id, id, name, description, deadline) VALUES (?, ?, ?, ?)";
        int rows = jdbcTemplate.update(
                sql,

                subproject.getProjectId(),
                subproject.getId(),
                subproject.getName(),
                subproject.getDescription(),
                subproject.getDeadline()
        );
        return rows > 0;
    }

    public boolean updateSubProject(SubProject subproject) {
        String sql = """
                UPDATE subproject
                SET project_id = ?, id = ?, name = ?, description = ?, deadline = ?
                WHERE id = ?
                """;
        int rows = jdbcTemplate.update(
                sql,
                subproject.getProjectId(),
                subproject.getName(),
                subproject.getDescription(),
                subproject.getDeadline(),
                subproject.getId()
        );
        return rows > 0;
    }

    public boolean deleteSubProject(long id) {
        String sql = "DELETE FROM subproject WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows > 0;
    }

    private static class SubProjectRowMapper implements RowMapper<SubProject> {
        @Override
        public SubProject mapRow(ResultSet rs, int rowNum) throws SQLException {
            SubProject sp = new SubProject();
            sp.setId(rs.getLong("id"));
            sp.setProjectId(rs.getLong("project_id"));
            sp.setName(rs.getString("name"));
            sp.setDescription(rs.getString("description"));
            LocalDate deadline = rs.getDate("deadline") != null
                    ? rs.getDate("deadline").toLocalDate()
                    : null;
            sp.setDeadline(deadline);
            return sp;
        }
    }
}
