package com.example.projectcalculator.model;

// SubProject.java
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SubProject {
    private Long id;
    private Long projectId;

    @NotBlank(message = "Sub-project name is required")
    @Size(min = 3, max = 50, message = "Sub-project name must be between 3 and 50 characters")
    private String name;

    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;

    @NotNull(message = "Deadline is required")
    private LocalDate deadline;

    private List<Task> tasks = new ArrayList<>();

    // constructors, getters, setters
    public SubProject() {}

    public SubProject(Long id, Long projectId, String name, String description, LocalDate deadline) {
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
    }

    // getters and setters for all fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public List<Task> getTasks() { return tasks; }
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }
}