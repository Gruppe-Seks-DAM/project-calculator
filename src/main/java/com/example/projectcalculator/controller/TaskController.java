package com.example.projectcalculator.controller;

import com.example.projectcalculator.dto.TaskDto;
import com.example.projectcalculator.model.Task;
import com.example.projectcalculator.service.TaskService;
import jakarta.validation.Valid;
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

    /**
     * #189 - POST /tasks/[id]/delete
     * Handle task deletion
     */
    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id, Model model) {
        try {
            // Get subproject ID for redirect
            Optional<Long> subProjectIdOpt = service.getSubProjectIdForTask(id);

            // Delete the task
            boolean deleted = service.deleteTask(id);

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
        Optional<Task> taskOpt = service.getTaskById(id);

        if (taskOpt.isEmpty()) {
            return "redirect:/projects";
        }

        model.addAttribute("task", taskOpt.get());
        return "tasks/confirm-delete";
    }

    // Existing methods (from previous implementation)
    @GetMapping("/create")
    public String showCreateForm(@RequestParam Long subProjectId, Model model) {}

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Task> opt = service.findById(id);
        if (opt.isEmpty()) return "redirect:/tasks?error=Task not found";

        Task t = opt.get();
        TaskDto dto = new TaskDto();
        dto.setName(t.getName());
        dto.setDescription(t.getDescription());
        dto.setDeadline(t.getDeadline());

        model.addAttribute("taskDto", dto);
        model.addAttribute("taskId", id);
        return "tasks/edit";
    }

    @PostMapping("/{id}")
    public String updateTask(
            @PathVariable Long id,
            @Valid @ModelAttribute("taskDto") TaskDto dto,
            BindingResult br,
            Model model
    ) {
        if (br.hasErrors()) {
            model.addAttribute("taskId", id);
            return "tasks/edit";
        }

        Task t = new Task();
        t.setId(id);
        t.setName(dto.getName());
        t.setDescription(dto.getDescription());
        t.setDeadline(dto.getDeadline());

        boolean updated = service.updateTask(t);
        if (!updated) {
            model.addAttribute("error","Failed to update task");
            model.addAttribute("taskId", id);
            return "tasks/edit";
        }
        return "redirect:/tasks?success=Task updated";

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
            service.createTask(task);
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
        List<Task> tasks = service.getTasksBySubProjectId(subProjectId);
        model.addAttribute("tasks", tasks);
        model.addAttribute("subProjectId", subProjectId);
        return "tasks/list";
    }
}