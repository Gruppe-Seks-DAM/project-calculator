package com.example.projectcalculator.repository;

import com.example.projectcalculator.model.SubProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubProjectRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private SubProjectRepository subProjectRepository;

    private SubProject sampleSubProject;
    private LocalDate sampleDeadline;

    @BeforeEach
    void setUp() {
        sampleDeadline = LocalDate.now().plusDays(14);
        // Note: The last parameter (tasks) is ignored in the model constructor
        sampleSubProject = new SubProject(1L, "Test SubProject",
                "Test Description", sampleDeadline, null);
    }

    @Test
    void findAllSubProjects_WhenNoSubProjectsExist_ShouldReturnEmptyList() {
        // Arrange
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenReturn(List.of());

        // Act
        List<SubProject> result = subProjectRepository.findAllSubProjects();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class));
    }

    @Test
    void createSubProject_WithValidData_ShouldReturnTrue() {
        // Arrange
        long projectId = 100L;
        when(jdbcTemplate.update(
                eq("INSERT INTO subproject (name, description, deadline, project_id) VALUES (?, ?, ?, ?)"),
                eq(sampleSubProject.getName()),
                eq(sampleSubProject.getDescription()),
                eq(sampleSubProject.getDeadline()),
                eq(projectId)
        )).thenReturn(1);

        // Act
        boolean result = subProjectRepository.createSubProject(sampleSubProject, projectId);

        // Assert
        assertTrue(result);
        verify(jdbcTemplate).update(
                eq("INSERT INTO subproject (name, description, deadline, project_id) VALUES (?, ?, ?, ?)"),
                eq(sampleSubProject.getName()),
                eq(sampleSubProject.getDescription()),
                eq(sampleSubProject.getDeadline()),
                eq(projectId)
        );
    }

    @Test
    void createSubProject_WhenInsertFails_ShouldReturnFalse() {
        // Arrange
        long projectId = 100L;
        when(jdbcTemplate.update(anyString(), any(), any(), any(), anyLong()))
                .thenReturn(0);

        // Act
        boolean result = subProjectRepository.createSubProject(sampleSubProject, projectId);

        // Assert
        assertFalse(result);
        verify(jdbcTemplate).update(anyString(), any(), any(), any(), anyLong());
    }

    @Test
    void createSubProject_WithNullDeadline_ShouldHandleNullValue() {
        // Arrange
        SubProject subProjectWithoutDeadline = new SubProject(
                null, "Test", "Description", null, null
        );
        long projectId = 100L;

        when(jdbcTemplate.update(anyString(), any(), any(), any(), anyLong()))
                .thenReturn(1);

        // Act
        boolean result = subProjectRepository.createSubProject(subProjectWithoutDeadline, projectId);

        // Assert
        assertTrue(result);
        verify(jdbcTemplate).update(
                anyString(),
                eq("Test"),
                eq("Description"),
                eq(null),  // null deadline
                eq(100L)
        );
    }

    @Test
    void delete_WithValidId_ShouldReturnTrue() {
        // Arrange
        long subProjectId = 1L;
        when(jdbcTemplate.update(eq("DELETE FROM subproject WHERE id = ?"), eq(subProjectId)))
                .thenReturn(1);

        // Act
        boolean result = subProjectRepository.delete(subProjectId);

        // Assert
        assertTrue(result);
        verify(jdbcTemplate).update(eq("DELETE FROM subproject WHERE id = ?"), eq(subProjectId));
    }

    @Test
    void delete_WhenNoRecordExists_ShouldReturnFalse() {
        // Arrange
        long subProjectId = 999L;
        when(jdbcTemplate.update(eq("DELETE FROM subproject WHERE id = ?"), eq(subProjectId)))
                .thenReturn(0);

        // Act
        boolean result = subProjectRepository.delete(subProjectId);

        // Assert
        assertFalse(result);
        verify(jdbcTemplate).update(eq("DELETE FROM subproject WHERE id = ?"), eq(subProjectId));
    }

    @Test
    void rowMapper_ShouldMapResultSetToSubProject() throws SQLException {
        // Arrange
        ResultSet rs = mock(ResultSet.class);
        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("name")).thenReturn("Test SubProject");
        when(rs.getString("description")).thenReturn("Test Description");
        when(rs.getDate("deadline")).thenReturn(Date.valueOf(sampleDeadline));

        SubProjectRepository.SubProjectRowMapper rowMapper =
                new SubProjectRepository.SubProjectRowMapper();

        // Act
        SubProject result = rowMapper.mapRow(rs, 1);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test SubProject", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertEquals(sampleDeadline, result.getDeadline());
        // Note: There's no tasks field in the model, so we don't assert it
    }

    @Test
    void rowMapper_WithNullDeadline_ShouldHandleNull() throws SQLException {
        // Arrange
        ResultSet rs = mock(ResultSet.class);
        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("name")).thenReturn("Test SubProject");
        when(rs.getString("description")).thenReturn("Test Description");
        when(rs.getDate("deadline")).thenReturn(null);  // null deadline

        SubProjectRepository.SubProjectRowMapper rowMapper =
                new SubProjectRepository.SubProjectRowMapper();

        // Act
        SubProject result = rowMapper.mapRow(rs, 1);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test SubProject", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertNull(result.getDeadline());  // Should be null
    }

    @Test
    void createSubProject_WithNullValues_ShouldHandleProperly() {
        // Arrange
        SubProject subProjectWithNulls = new SubProject(
                null, null, null, null, null
        );
        long projectId = 100L;

        when(jdbcTemplate.update(anyString(), any(), any(), any(), anyLong()))
                .thenReturn(1);

        // Act
        boolean result = subProjectRepository.createSubProject(subProjectWithNulls, projectId);

        // Assert
        assertTrue(result);
        verify(jdbcTemplate).update(
                anyString(),
                eq(null),  // null name
                eq(null),  // null description
                eq(null),  // null deadline
                eq(100L)
        );
    }

    @Test
    void delete_ShouldVerifySqlSyntax() {
        // Arrange
        long subProjectId = 1L;
        String expectedSql = "DELETE FROM subproject WHERE id = ?";

        when(jdbcTemplate.update(eq(expectedSql), eq(subProjectId)))
                .thenReturn(1);

        // Act
        boolean result = subProjectRepository.delete(subProjectId);

        // Assert
        assertTrue(result);
        verify(jdbcTemplate).update(eq(expectedSql), eq(subProjectId));
    }

    @Test
    void createSubProject_ShouldVerifySqlSyntax() {
        // Arrange
        long projectId = 100L;
        String expectedSql = "INSERT INTO subproject (name, description, deadline, project_id) VALUES (?, ?, ?, ?)";

        when(jdbcTemplate.update(
                eq(expectedSql),
                eq(sampleSubProject.getName()),
                eq(sampleSubProject.getDescription()),
                eq(sampleSubProject.getDeadline()),
                eq(projectId)
        )).thenReturn(1);

        // Act
        boolean result = subProjectRepository.createSubProject(sampleSubProject, projectId);

        // Assert
        assertTrue(result);
        verify(jdbcTemplate).update(
                eq(expectedSql),
                eq(sampleSubProject.getName()),
                eq(sampleSubProject.getDescription()),
                eq(sampleSubProject.getDeadline()),
                eq(projectId)
        );
    }

    @Test
    void rowMapper_WithEmptyDescription_ShouldHandleEmptyString() throws SQLException {
        // Arrange
        ResultSet rs = mock(ResultSet.class);
        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("name")).thenReturn("Test SubProject");
        when(rs.getString("description")).thenReturn("");  // empty description
        when(rs.getDate("deadline")).thenReturn(Date.valueOf(sampleDeadline));

        SubProjectRepository.SubProjectRowMapper rowMapper =
                new SubProjectRepository.SubProjectRowMapper();

        // Act
        SubProject result = rowMapper.mapRow(rs, 1);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test SubProject", result.getName());
        assertEquals("", result.getDescription());  // empty string
        assertEquals(sampleDeadline, result.getDeadline());
    }

    @Test
    void createSubProject_WithEmptyName_ShouldHandleEmptyString() {
        // Arrange
        SubProject subProjectWithEmptyName = new SubProject(
                null, "", "Description", sampleDeadline, null
        );
        long projectId = 100L;

        when(jdbcTemplate.update(anyString(), any(), any(), any(), anyLong()))
                .thenReturn(1);

        // Act
        boolean result = subProjectRepository.createSubProject(subProjectWithEmptyName, projectId);

        // Assert
        assertTrue(result);
        verify(jdbcTemplate).update(
                anyString(),
                eq(""),  // empty name
                eq("Description"),
                eq(sampleDeadline),
                eq(100L)
        );
    }

    @Test
    void delete_WithZeroId_ShouldHandleEdgeCase() {
        // Arrange
        long subProjectId = 0L;
        when(jdbcTemplate.update(eq("DELETE FROM subproject WHERE id = ?"), eq(subProjectId)))
                .thenReturn(0);  // No record with ID 0

        // Act
        boolean result = subProjectRepository.delete(subProjectId);

        // Assert
        assertFalse(result);
        verify(jdbcTemplate).update(eq("DELETE FROM subproject WHERE id = ?"), eq(subProjectId));
    }

    @Test
    void findAllSubProjects_WhenDatabaseThrowsException_ShouldPropagateException() {
        // Arrange
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            subProjectRepository.findAllSubProjects();
        });

        verify(jdbcTemplate).query(anyString(), any(RowMapper.class));
    }

    @Test
    void createSubProject_WhenDatabaseThrowsException_ShouldPropagateException() {
        // Arrange
        long projectId = 100L;
        when(jdbcTemplate.update(anyString(), any(), any(), any(), anyLong()))
                .thenThrow(new RuntimeException("Constraint violation"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            subProjectRepository.createSubProject(sampleSubProject, projectId);
        });

        verify(jdbcTemplate).update(anyString(), any(), any(), any(), anyLong());
    }
}