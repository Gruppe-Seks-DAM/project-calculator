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
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * #189 - POST /tasks/[id]/delete
     * Handle task deletion
     */
    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id, Model model) {
        try {
            // Get subproject ID for redirect
            Optional<Long> subProjectIdOpt = taskService.getSubProjectIdForTask(id);

            // Delete the task
            boolean deleted = taskService.deleteTask(id);

            if (deleted) {
                // Redirect back to subproject tasks page
                if (subProjectIdOpt.isPresent()) {
                    return "redirect:/subprojects/" + subProjectIdOpt.get();
                } else {
                    return "redirect:/projects";
                }
            } else {
                model.addAttribute("error", "Task not found or could not be deleted");
                // Try to redirect anyway, or show error page
                return "redirect:/projects";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error deleting task: " + e.getMessage());
            return "redirect:/projects";
        }
    }

    // Alternative: Delete with confirmation (GET request for confirmation page)
    @GetMapping("/{id}/delete")
    public String confirmDelete(@PathVariable Long id, Model model) {
        Optional<Task> taskOpt = taskService.getTaskById(id);

        if (taskOpt.isEmpty()) {
            return "redirect:/projects";
        }

        model.addAttribute("task", taskOpt.get());
        return "tasks/confirm-delete";
    }

    // Existing methods (from previous implementation)
    @GetMapping("/create")
    public String showCreateForm(@RequestParam Long subProjectId, Model model) {
        Task task = new Task();
        task.setSubProjectId(subProjectId);
        model.addAttribute("task", task);
        return "tasks/create";
    }

    @PostMapping("/create")
    public String createTask(
            @RequestParam Long subProjectId,
            @Valid @ModelAttribute("task") Task task,
            BindingResult bindingResult,
            Model model) {

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

    @GetMapping("/subproject/{subProjectId}")
    public String viewSubProjectTasks(@PathVariable Long subProjectId, Model model) {
        List<Task> tasks = taskService.getTasksBySubProjectId(subProjectId);
        model.addAttribute("tasks", tasks);
        model.addAttribute("subProjectId", subProjectId);
        return "tasks/list";
    }
}