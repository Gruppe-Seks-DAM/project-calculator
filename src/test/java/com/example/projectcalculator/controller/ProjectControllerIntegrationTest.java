package com.example.projectcalculator.controller;

import com.example.projectcalculator.dto.ProjectDto;
import com.example.projectcalculator.model.Project;
import com.example.projectcalculator.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
class ProjectControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @Test
    void createProject_WithBlankName_ShouldShowValidationError() throws Exception {
        // Act & Assert - name is required
        mockMvc.perform(post("/projects")
                        .param("name", "")
                        .param("description", "Valid Description"))
                .andExpect(status().isOk())
                .andExpect(view().name("createProjectForm"))
                .andExpect(model().attributeHasFieldErrors("projectDto", "name"))
                .andExpect(model().errorCount(1));
    }

    @Test
    void createProject_WithVeryLongName_ShouldShowValidationError() throws Exception {
        // Arrange
        String veryLongName = "A".repeat(256); // Assuming name max length is 255

        // Act & Assert
        mockMvc.perform(post("/projects")
                        .param("name", veryLongName)
                        .param("description", "Valid Description"))
                .andExpect(status().isOk())
                .andExpect(view().name("createProjectForm"))
                .andExpect(model().attributeHasFieldErrors("projectDto", "name"));
    }

    @Test
    void showProjects_ShouldDisplayCorrectNumberOfProjects() throws Exception {
        // Arrange
        Project project1 = new Project();
        project1.setId(1L);
        project1.setName("Project 1");

        Project project2 = new Project();
        project2.setId(2L);
        project2.setName("Project 2");

        when(projectService.getAllProjects()).thenReturn(List.of(project1, project2));

        // Act & Assert
        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(view().name("projects"))
                .andExpect(model().attribute("projects", hasSize(2)))
                .andExpect(model().attribute("projects", hasItem(
                        allOf(
                                hasProperty("id", is(1L)),
                                hasProperty("name", is("Project 1"))
                        )
                )));
    }
}