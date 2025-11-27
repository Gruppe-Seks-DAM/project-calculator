package com.example.projectcalculator.model;// Project.java
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Project {
    private Long id;

    @NotBlank(message = "Project name is required")
    @Size(min = 3, max = 50, message = "Project name must be between 3 and 50 characters")
    private String name;

    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;

    // Removed @NotNull since your schema allows NULL
    private LocalDate deadline;

    private List<SubProject> subProjects = new ArrayList<>();

    // constructors, getters, setters
    public Project() {}

    public Project(Long id, String name, String description, LocalDate deadline) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
    }


    // getters and setters for all fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public List<SubProject> getSubProjects() { return subProjects; }
    public void setSubProjects(List<SubProject> subProjects) { this.subProjects = subProjects; }
}