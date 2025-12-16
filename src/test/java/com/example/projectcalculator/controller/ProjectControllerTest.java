package com.example.projectcalculator.controller;

import com.example.projectcalculator.dto.ProjectDto;
import com.example.projectcalculator.model.Project;
import com.example.projectcalculator.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private ProjectController projectController;

    @Captor
    private ArgumentCaptor<ProjectDto> projectDtoCaptor;

    private MockMvc mockMvc;
    private ProjectDto sampleProjectDto;
    private Project sampleProject;

    @BeforeEach
    void setUp() {
        // Initialize MockMvc standalone setup
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();

        // Setup sample data
        sampleProjectDto = new ProjectDto();
        sampleProjectDto.setName("Test Project");
        sampleProjectDto.setDescription("Test Description");

        sampleProject = new Project();
        sampleProject.setId(1L);
        sampleProject.setName("Test Project");
        sampleProject.setDescription("Test Description");
    }

    // Test for POST /projects (createProject) - Invalid input
    @Test
    void createProject_WithInvalidInput_ShouldReturnToForm() throws Exception {
        // Arrange
        // We need to test validation, but validation happens before controller method
        // In a real test, you'd need to test validation separately or use form objects

        // This test shows what happens when BindingResult has errors
        // For actual validation testing, see integration tests below

        // Act & Assert - empty name should trigger validation
        mockMvc.perform(post("/projects")
                        .param("name", "")  // Empty name - invalid
                        .param("description", "Description"))
                .andExpect(status().isOk())  // Not 3xx because validation fails
                .andExpect(view().name("createProjectForm"));
    }

    @Test
    void createProject_WithBindingErrors_ShouldReturnToForm() throws Exception {
        // This test is for the controller method logic when BindingResult has errors
        // Since we can't easily mock BindingResult in MockMvc, we test the method directly

        // Arrange
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = projectController.createProject(sampleProjectDto, bindingResult);

        // Assert
        assertThat(viewName).isEqualTo("createProjectForm");
        verify(projectService, never()).create(any(ProjectDto.class));
    }

    // Test for GET /projects/debug/list (debugList) - API endpoint
    @Test
    void debugList_ShouldReturnJsonList() throws Exception {
        // Arrange
        List<Project> projects = Arrays.asList(sampleProject);
        when(projectService.getAllProjects()).thenReturn(projects);

        // Act & Assert
        mockMvc.perform(get("/projects/debug/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Project"))
                .andExpect(jsonPath("$[0].description").value("Test Description"));

        verify(projectService).getAllProjects();
    }

    @Test
    void debugList_WhenNoProjects_ShouldReturnEmptyJsonArray() throws Exception {
        // Arrange
        when(projectService.getAllProjects()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/projects/debug/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(projectService).getAllProjects();
    }

    @Test
    void showProjects_ShouldAddProjectsToModel() {
        // Arrange
        List<Project> projects = Arrays.asList(sampleProject);
        when(projectService.getAllProjects()).thenReturn(projects);

        // Act
        String viewName = projectController.showProjects(model);

        // Assert
        assertThat(viewName).isEqualTo("projects");
        verify(model).addAttribute(eq("projects"), eq(projects));
        verify(projectService).getAllProjects();
    }

    @Test
    void showCreateForm_ShouldAddEmptyProjectDtoToModel() {
        // Act
        String viewName = projectController.showCreateForm(model);

        // Assert
        assertThat(viewName).isEqualTo("createProjectForm");
        verify(model).addAttribute(eq("projectDto"), any(ProjectDto.class));
    }
}