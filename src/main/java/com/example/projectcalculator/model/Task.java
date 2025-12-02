package com.example.projectcalculator.model;// Task.java
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Task {
    private Long id;
    private Long subProjectId;

    @NotBlank(message = "Task name is required")
    @Size(min = 3, max = 50, message = "Task name must be between 3 and 50 characters")
    private String name;

    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;

    @NotNull(message = "Deadline is required")
    private LocalDate deadline;

    private List<SubTask> subTasks = new ArrayList<>();

    // constructors, getters, setters
    public Task() {}

    public Task(Long id, Long subProjectId, String name, String description, LocalDate deadline) {
        this.id = id;
        this.subProjectId = subProjectId;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
    }

    // getters and setters for all fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSubProjectId() { return subProjectId; }
    public void setSubProjectId(Long subProjectId) { this.subProjectId = subProjectId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public List<SubTask> getSubTasks() { return subTasks; }
    public void setSubTasks(List<SubTask> subTasks) { this.subTasks = subTasks; }
}