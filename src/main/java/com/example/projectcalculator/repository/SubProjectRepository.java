package com.example.projectcalculator.repository;

import com.example.projectcalculator.model.SubProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public class SubProjectRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SubProjectRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<SubProject> subProjectRowMapper = new RowMapper<SubProject>() {
        @Override
        public SubProject mapRow(ResultSet rs, int rowNum) throws SQLException {
            SubProject subProject = new SubProject();
            subProject.setId(rs.getLong("id"));
            subProject.setProjectId(rs.getLong("project_id"));
            subProject.setName(rs.getString("name"));
            subProject.setDescription(rs.getString("description"));

            Date deadline = rs.getDate("deadline");
            if (deadline != null) {
                subProject.setDeadline(deadline.toLocalDate());
            }

            return subProject;
        }
    };

    /**
     * Find a subproject by ID
     */
    public Optional<SubProject> findById(Long id) {
        String sql = "SELECT * FROM subproject WHERE id = ?";
        try {
            SubProject subProject = jdbcTemplate.queryForObject(sql, subProjectRowMapper, id);
            return Optional.ofNullable(subProject);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Update an existing subproject
     * #170 - Repository: update(SubProject)
     */
    public int update(SubProject subProject) {
        String sql = """
            UPDATE subproject 
            SET name = ?, description = ?, deadline = ?
            WHERE id = ? AND project_id = ?
            """;

        Date deadline = subProject.getDeadline() != null ?
                Date.valueOf(subProject.getDeadline()) : null;

        return jdbcTemplate.update(
                sql,
                subProject.getName(),
                subProject.getDescription(),
                deadline,
                subProject.getId(),
                subProject.getProjectId()
        );
    }

    /**
     * Check if a subproject exists by ID and project ID
     */
    public boolean existsByIdAndProjectId(Long id, Long projectId) {
        String sql = "SELECT COUNT(*) FROM subproject WHERE id = ? AND project_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id, projectId);
        return count != null && count > 0;
    }
}