package com.example.projectcalculator.controller;

import com.example.projectcalculator.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProjectController {
    ProjectService service;

    /*

    Tilføjes til liste view:

    (delete knappen)
    <form th:action="@{/projects/{id}/delete(id=${project.id})}" method="post" style="display:inline;">
    <button type="submit">Delete</button>
</form>


<div th:if="${param.success}" class="alert alert-success">
    <p th:text="${param.success}"></p>
</div>

<div th:if="${param.error}" class="alert alert-danger">
    <p th:text="${param.error}"></p>
</div>

     */

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable long id) {

        boolean deleted = service.delete(id);

        if (!deleted) {
            return "redirect:/projects?error=Could not delete project";
        }

        return "redirect:/projects?success=Project deleted successfully";
    }
}