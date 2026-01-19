package com.example.projectcalculator.controller;

import com.example.projectcalculator.model.SubTask;
import com.example.projectcalculator.service.SubTaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/projects/{projectId}/subprojects/{subProjectId}/tasks/{taskId}/subtasks")
public class SubTaskController {

    private final SubTaskService service;

    public SubTaskController(SubTaskService service) {
        this.service = service;
    }

    @GetMapping
    public String showSubTasks(@PathVariable long projectId,
                               @PathVariable long subProjectId,
                               @PathVariable long taskId,
                               Model model) {

        model.addAttribute("projectId", projectId);
        model.addAttribute("subProjectId", subProjectId);
        model.addAttribute("taskId", taskId);
        model.addAttribute("subtasks", service.getAllSubTasks(taskId));
        return "subtasks";
    }

    @GetMapping("/create")
    public String showCreateSubTaskForm(@PathVariable long projectId,
                                        @PathVariable long subProjectId,
                                        @PathVariable long taskId,
                                        Model model) {

        SubTask subtask = new SubTask();
        subtask.setTaskId(taskId);

        model.addAttribute("projectId", projectId);
        model.addAttribute("subProjectId", subProjectId);
        model.addAttribute("taskId", taskId);
        model.addAttribute("subtask", subtask);

        return "createSubtaskForm";
    }

    @PostMapping("/create")
    public String createSubTask(@PathVariable long projectId,
                                @PathVariable long subProjectId,
                                @PathVariable long taskId,
                                @ModelAttribute("subtask") SubTask subtask) {

        subtask.setTaskId(taskId);

        boolean created = service.createSubTask(subtask);

        if (!created) {
            return "redirect:/projects/" + projectId + "/subprojects/" + subProjectId +
                    "/tasks/" + taskId + "/subtasks?error=Could not create subtask";
        }

        return "redirect:/projects/" + projectId + "/subprojects/" + subProjectId +
                "/tasks/" + taskId + "/subtasks?success=Subtask created successfully";
    }

    @GetMapping("/{id}/edit")
    public String showUpdateForm(@PathVariable long projectId,
                                 @PathVariable long subProjectId,
                                 @PathVariable long taskId,
                                 @PathVariable long id,
                                 Model model) {

        SubTask subtask = service.getSubTaskById(taskId, id);

        if (subtask == null) {
            return "redirect:/projects/" + projectId + "/subprojects/" + subProjectId +
                    "/tasks/" + taskId + "/subtasks?error=Subtask not found";
        }

        model.addAttribute("projectId", projectId);
        model.addAttribute("subProjectId", subProjectId);
        model.addAttribute("taskId", taskId);
        model.addAttribute("subtask", subtask);

        return "editSubtaskForm";
    }

    @PostMapping("/{id}/edit")
    public String updateSubTask(@PathVariable long projectId,
                                @PathVariable long subProjectId,
                                @PathVariable long taskId,
                                @PathVariable long id,
                                @ModelAttribute("subtask") SubTask subtask) {

        subtask.setId(id);
        subtask.setTaskId(taskId);

        boolean updated = service.updateSubTask(subtask);

        if (!updated) {
            return "redirect:/projects/" + projectId + "/subprojects/" + subProjectId +
                    "/tasks/" + taskId + "/subtasks?error=Could not update subtask";
        }

        return "redirect:/projects/" + projectId + "/subprojects/" + subProjectId +
                "/tasks/" + taskId + "/subtasks?success=Subtask updated successfully";
    }

    @PostMapping("/{id}/delete")
    public String deleteSubTask(@PathVariable long projectId,
                                @PathVariable long subProjectId,
                                @PathVariable long taskId,
                                @PathVariable long id) {

        boolean deleted = service.deleteSubTask(taskId, id);

        if (!deleted) {
            return "redirect:/projects/" + projectId + "/subprojects/" + subProjectId +
                    "/tasks/" + taskId + "/subtasks?error=Could not delete subtask";
        }

        return "redirect:/projects/" + projectId + "/subprojects/" + subProjectId +
                "/tasks/" + taskId + "/subtasks?success=Subtask deleted successfully";
    }
}
