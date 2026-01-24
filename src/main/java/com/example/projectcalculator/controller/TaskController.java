package com.example.projectcalculator.controller;

import com.example.projectcalculator.model.Task;
import com.example.projectcalculator.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/projects/{projectId}/subprojects/{subProjectId}/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public String showTasks(@PathVariable long projectId,
                            @PathVariable long subProjectId,
                            Model model) {

        model.addAttribute("projectId", projectId);
        model.addAttribute("subProjectId", subProjectId);
        model.addAttribute("tasks", service.getAllTasks(subProjectId));
        return "task/list";
    }

    @GetMapping("/create")
    public String showCreateTaskForm(@PathVariable long projectId,
                                     @PathVariable long subProjectId,
                                     Model model) {

        Task task = new Task();
        task.setSubProjectId(subProjectId);

        model.addAttribute("projectId", projectId);
        model.addAttribute("subProjectId", subProjectId);
        model.addAttribute("task", task);

        return "task/create";
    }

    @PostMapping("/create")
    public String createTask(@PathVariable long projectId,
                             @PathVariable long subProjectId,
                             @ModelAttribute("task") Task task) {

        task.setSubProjectId(subProjectId);

        boolean created = service.createTask(task);

        if (!created) {
            return "redirect:/projects/" + projectId + "/subprojects/" + subProjectId +
                    "/tasks?error=Could not create task";
        }

        return "redirect:/projects/" + projectId + "/subprojects/" + subProjectId +
                "/tasks?success=Task created successfully";
    }

    @GetMapping("/{id}/edit")
    public String showUpdateForm(@PathVariable long projectId,
                                 @PathVariable long subProjectId,
                                 @PathVariable long id,
                                 Model model) {

        Task task = service.getTaskById(subProjectId, id);

        if (task == null) {
            return "redirect:/projects/" + projectId + "/subprojects/" + subProjectId +
                    "/tasks?error=Task not found";
        }

        model.addAttribute("projectId", projectId);
        model.addAttribute("subProjectId", subProjectId);
        model.addAttribute("task", task);

        return "task/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateTask(@PathVariable long projectId,
                             @PathVariable long subProjectId,
                             @PathVariable long id,
                             @ModelAttribute("task") Task task) {

        task.setId(id);
        task.setSubProjectId(subProjectId);

        boolean updated = service.updateTask(task);

        if (!updated) {
            return "redirect:/projects/" + projectId + "/subprojects/" + subProjectId +
                    "/tasks?error=Could not update task";
        }

        return "redirect:/projects/" + projectId + "/subprojects/" + subProjectId +
                "/tasks?success=Task updated successfully";
    }

    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable long projectId,
                             @PathVariable long subProjectId,
                             @PathVariable long id) {

        boolean deleted = service.deleteTask(subProjectId, id);

        if (!deleted) {
            return "redirect:/projects/" + projectId + "/subprojects/" + subProjectId +
                    "/tasks?error=Could not delete task";
        }

        return "redirect:/projects/" + projectId + "/subprojects/" + subProjectId +
                "/tasks?success=Task deleted successfully";
    }
}
