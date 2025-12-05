package com.example.projectcalculator.controller;

import com.example.projectcalculator.model.Task;
import com.example.projectcalculator.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/subprojects")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Show form to create a new task
     */
    @GetMapping("/{subProjectId}/tasks/create")
    public String showCreateForm(@PathVariable Long subProjectId, Model model) {
        Task task = new Task();
        task.setSubProjectId(subProjectId);
        model.addAttribute("task", task);
        return "tasks/create";
    }

    /**
     * #182 - POST /subprojects/{id}/tasks
     * Handle form submission for creating a task
     */
    @PostMapping("/{subProjectId}/tasks")
    public String createTask(
            @PathVariable Long subProjectId,
            @Valid @ModelAttribute("task") Task task,
            BindingResult bindingResult,
            Model model) {

        // Ensure the task is linked to the correct subproject
        task.setSubProjectId(subProjectId);

        if (bindingResult.hasErrors()) {
            return "tasks/create";
        }

        try {
            taskService.createTask(task);
            return "redirect:/subprojects/" + subProjectId;
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "tasks/create";
        }
    }

    /**
     * View tasks for a subproject
     */
    @GetMapping("/{subProjectId}")
    public String viewSubProjectTasks(@PathVariable Long subProjectId, Model model) {
        List<Task> tasks = taskService.getTasksBySubProjectId(subProjectId);
        model.addAttribute("tasks", tasks);
        model.addAttribute("subProjectId", subProjectId);
        return "tasks/list";
    }
}