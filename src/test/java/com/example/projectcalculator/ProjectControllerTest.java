package com.example.projectcalculator;// ProjectControllerTest.java
import com.example.projectcalculator.controller.ProjectController;
import com.example.projectcalculator.model.Project;
import com.example.projectcalculator.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @Test
    void showEditForm_WithValidId_ReturnsEditView() throws Exception {
        // Arrange - Using your actual schema structure
        Project project = new Project(1L, "Test Project", "Test Description", LocalDate.now().plusDays(30));
        when(projectService.findById(1L)).thenReturn(Optional.of(project));

        // Act & Assert
        mockMvc.perform(get("/projects/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("project/edit"))
                .andExpect(model().attributeExists("project"))
                .andExpect(model().attribute("project", project));
    }

    @Test
    void updateProject_WithValidData_RedirectsToProjects() throws Exception {
        // Arrange
        when(projectService.updateProject(any(Project.class))).thenReturn(true);

        // Act & Assert - Note: deadline can be null in your schema
        mockMvc.perform(post("/projects/1/edit")
                        .param("id", "1")
                        .param("name", "Updated Project")
                        .param("description", "Updated Description")
                        .param("deadline", "")) // Empty deadline allowed in your schema
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects?success=Project updated successfully"));
    }

    @Test
    void updateProject_WithInvalidName_ReturnsEditView() throws Exception {
        // Arrange
        Project project = new Project(1L, "Test Project", "Test Description", null); // null deadline allowed
        when(projectService.findById(1L)).thenReturn(Optional.of(project));

        // Act & Assert - Name too short
        mockMvc.perform(post("/projects/1/edit")
                        .param("id", "1")
                        .param("name", "A") // Too short - should trigger validation
                        .param("description", "Updated Description")
                        .param("deadline", "")) // Empty string for null deadline
                .andExpect(status().isOk())
                .andExpect(view().name("project/edit"))
                .andExpect(model().attributeHasErrors("project"))
                .andExpect(model().attributeHasFieldErrors("project", "name"));
    }
}