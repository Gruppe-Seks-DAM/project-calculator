package com.example.projectcalculator.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectRepository {

    private final JdbcTemplate jdbc;

    public ProjectRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean delete(long id) {
        String sql = "DELETE FROM project WHERE id = ?";
        int rows = jdbc.update(sql, id);
        return rows > 0;
    }
}