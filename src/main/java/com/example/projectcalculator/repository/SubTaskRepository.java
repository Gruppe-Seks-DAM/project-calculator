package com.example.projectcalculator.repository;

import com.example.projectcalculator.model.Project;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class SubTaskRepository {

    private final JdbcTemplate jdbcTemplate;

    public SubTaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean createSubTask() {
        // Implementation goes here
        return false;
    }
}
