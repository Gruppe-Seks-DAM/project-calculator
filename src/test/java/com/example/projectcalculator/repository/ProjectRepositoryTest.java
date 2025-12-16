package com.example.projectcalculator.repository;

import com.example.projectcalculator.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ProjectRepository projectRepository;

    @Captor
    private ArgumentCaptor<String> sqlCaptor;

    @Captor
    private ArgumentCaptor<Object[]> paramsCaptor;

    @Captor
    private ArgumentCaptor<RowMapper<Project>> rowMapperCaptor;

    private Project testProject;
    private LocalDate testDeadline;

    @BeforeEach
    void setUp() {
        testDeadline = LocalDate.of(2024, 12, 31);
        testProject = new Project(1L, "Test Project", "Test Description", testDeadline);
    }

    @Test
    void create_ShouldReturnTrue_WhenInsertSucceeds() {
        // Arrange
        when(jdbcTemplate.update(
                anyString(),
                eq(testProject.getName()),
                eq(testProject.getDescription()),
                eq(testProject.getDeadline())
        )).thenReturn(1);

        // Act
        boolean result = projectRepository.create(testProject);

        // Assert
        assertTrue(result);
        verify(jdbcTemplate).update(
                sqlCaptor.capture(),
                eq(testProject.getName()),
                eq(testProject.getDescription()),
                eq(testProject.getDeadline())
        );
        assertTrue(sqlCaptor.getValue().contains("INSERT INTO project"));
    }

    @Test
    void create_ShouldReturnFalse_WhenInsertFails() {
        // Arrange
        when(jdbcTemplate.update(
                anyString(),
                anyString(),
                anyString(),
                any(LocalDate.class)
        )).thenReturn(0);

        // Act
        boolean result = projectRepository.create(testProject);

        // Assert
        assertFalse(result);
    }

    @Test
    void findAllProjects_ShouldReturnListOfProjects() {
        // Arrange
        List<Project> expectedProjects = List.of(testProject);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenReturn(expectedProjects);

        // Act
        List<Project> result = projectRepository.findAllProjects();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProject, result.get(0));
        verify(jdbcTemplate).query(
                sqlCaptor.capture(),
                rowMapperCaptor.capture()
        );
        assertTrue(sqlCaptor.getValue().contains("SELECT id, name, description, deadline"));
        assertTrue(sqlCaptor.getValue().contains("ORDER BY id"));
    }

    @Test
    void findAllProjects_ShouldReturnEmptyList_WhenNoProjects() {
        // Arrange
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenReturn(Collections.emptyList());

        // Act
        List<Project> result = projectRepository.findAllProjects();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void createProject_ShouldReturnTrue_WhenInsertSucceeds() {
        // Arrange
        when(jdbcTemplate.update(
                anyString(),
                eq(testProject.getName()),
                eq(testProject.getDescription()),
                eq(testProject.getDeadline())
        )).thenReturn(1);

        // Act
        boolean result = projectRepository.createProject(testProject);

        // Assert
        assertTrue(result);
        verify(jdbcTemplate).update(
                sqlCaptor.capture(),
                eq(testProject.getName()),
                eq(testProject.getDescription()),
                eq(testProject.getDeadline())
        );
        assertEquals("INSERT INTO project (name, description, deadline) VALUES (?, ?, ?)", sqlCaptor.getValue());
    }

    @Test
    void deleteProject_ShouldReturnTrue_WhenDeleteSucceeds() {
        // Arrange
        long projectId = 1L;
        when(jdbcTemplate.update(anyString(), eq(projectId))).thenReturn(1);

        // Act
        boolean result = projectRepository.deleteProject(projectId);

        // Assert
        assertTrue(result);
        verify(jdbcTemplate).update(sqlCaptor.capture(), eq(projectId));
        assertEquals("DELETE FROM project WHERE id = ?", sqlCaptor.getValue());
    }

    @Test
    void deleteProject_ShouldReturnFalse_WhenDeleteFails() {
        // Arrange
        long projectId = 999L;
        when(jdbcTemplate.update(anyString(), eq(projectId))).thenReturn(0);

        // Act
        boolean result = projectRepository.deleteProject(projectId);

        // Assert
        assertFalse(result);
    }

    @Test
    void delete_ShouldReturnTrue_WhenDeleteSucceeds() {
        // Arrange
        long projectId = 1L;
        when(jdbcTemplate.update(anyString(), eq(projectId))).thenReturn(1);

        // Act
        boolean result = projectRepository.delete(projectId);

        // Assert
        assertTrue(result);
        verify(jdbcTemplate).update(sqlCaptor.capture(), eq(projectId));
        assertEquals("DELETE FROM project WHERE id = ?", sqlCaptor.getValue());
    }

    @Test
    void delete_ShouldReturnFalse_WhenDeleteFails() {
        // Arrange
        long projectId = 999L;
        when(jdbcTemplate.update(anyString(), eq(projectId))).thenReturn(0);

        // Act
        boolean result = projectRepository.delete(projectId);

        // Assert
        assertFalse(result);
    }

    @Test
    void projectRowMapper_ShouldMapResultSetCorrectly() throws SQLException {
        // Arrange
        ProjectRepository.ProjectRowMapper rowMapper = new ProjectRepository.ProjectRowMapper();
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockResultSet.getLong("id")).thenReturn(1L);
        when(mockResultSet.getString("name")).thenReturn("Test Project");
        when(mockResultSet.getString("description")).thenReturn("Test Description");
        when(mockResultSet.getDate("deadline")).thenReturn(Date.valueOf(testDeadline));

        // Act
        Project result = rowMapper.mapRow(mockResultSet, 1);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Project", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertEquals(testDeadline, result.getDeadline());
    }

    @Test
    void projectRowMapper_ShouldHandleNullDeadline() throws SQLException {
        // Arrange
        ProjectRepository.ProjectRowMapper rowMapper = new ProjectRepository.ProjectRowMapper();
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockResultSet.getLong("id")).thenReturn(1L);
        when(mockResultSet.getString("name")).thenReturn("Test Project");
        when(mockResultSet.getString("description")).thenReturn("Test Description");
        when(mockResultSet.getDate("deadline")).thenReturn(null);

        // Act
        Project result = rowMapper.mapRow(mockResultSet, 1);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Project", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertNull(result.getDeadline());
    }

    @Test
    void create_ShouldThrowException_WhenDatabaseErrorOccurs() {
        // Arrange
        when(jdbcTemplate.update(
                anyString(),
                anyString(),
                anyString(),
                any(LocalDate.class)
        )).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            projectRepository.create(testProject);
        });
    }

    @Test
    void findAllProjects_ShouldThrowException_WhenDatabaseErrorOccurs() {
        // Arrange
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            projectRepository.findAllProjects();
        });
    }

    @Test
    void testDuplicateMethods_SameBehavior() {
        // Both create() and createProject() should behave the same way
        Project project1 = new Project(null, "Project 1", "Desc 1", LocalDate.now());
        Project project2 = new Project(null, "Project 2", "Desc 2", LocalDate.now());

        when(jdbcTemplate.update(anyString(), any(), any(), any()))
                .thenReturn(1)
                .thenReturn(0);

        // Act
        boolean result1 = projectRepository.create(project1);
        boolean result2 = projectRepository.createProject(project2);

        // Assert
        assertTrue(result1);
        assertFalse(result2);

        // Verify both methods were called
        verify(jdbcTemplate, times(2)).update(anyString(), any(), any(), any());
    }
}