package com.example.projectcalculator.controller;

import com.example.projectcalculator.service.SubProjectService;
import com.example.projectcalculator.model.SubProject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/projects/{projectId}/subprojects")
public class SubProjectController {

    ///  DEPENDENCY INJECTION OF THE SUBPROJECT SERVICE
    private final SubProjectService service;

    public SubProjectController(SubProjectService service) {
        this.service = service;
    }

    ///  LISTS ALL SUBPROJECTS FOR A SPECIFIC PROJECT AND ADDS THEM TO THE MODEL
    @GetMapping
    public String showSubprojects(@PathVariable long projectId,Model model) {
        model.addAttribute("projectId", projectId);
        model.addAttribute("subprojects", service.getAllSubProjects(projectId));
        return "subproject/list";
    }

    ///  SHOW FORM TO CREATE A NEW SUBPROJECT (FOR THIS PROJECT)
    @GetMapping("/create")
    public String showCreateSubProjectForm(@PathVariable long projectId, Model model) {
        SubProject subproject = new SubProject();
        subproject.setProjectId(projectId); // bind relationship

        model.addAttribute("projectId", projectId);
        model.addAttribute("subproject", subproject);

        return "subproject/create";
    }

    ///  CREATE A NEW SUBPROJECT AND REDIRECT BACK TO THIS PROJECT'S SUBPROJECTS
    @PostMapping("/create")
    public String createSubproject(@PathVariable long projectId,
                                   @ModelAttribute("subproject") SubProject subproject) {

        subproject.setProjectId(projectId); // enforce correct FK (important)

        boolean created = service.createSubProject(subproject);

        if (!created) {
            return "redirect:/projects/" + projectId + "/subprojects?error=Could not create subproject";
        }

        return "redirect:/projects/" + projectId + "/subprojects?success=Subproject created successfully";
    }

    /// SHOW FORM TO UPDATE AN EXISTING SUBPROJECT (FOR THIS PROJECT)
    @GetMapping("/{id}/edit")
    public String showUpdateForm(@PathVariable long projectId,
                                 @PathVariable long id,
                                 Model model) {

        SubProject subproject = service.getSubProjectById(projectId, id);

        if (subproject == null) {
            return "redirect:/projects/" + projectId + "/subprojects?error=Subproject not found";
        }

        model.addAttribute("projectId", projectId);
        model.addAttribute("subproject", subproject);

        return "subproject/edit";
    }

    /// UPDATE AN EXISTING SUBPROJECT AND REDIRECT BACK TO THIS PROJECT'S SUBPROJECTS
    @PostMapping("/{id}/edit")
    public String updateSubproject(@PathVariable long projectId,
                                   @PathVariable long id,
                                   @ModelAttribute("subproject") SubProject subproject) {

        subproject.setId(id);              // ensure correct ID (important)
        subproject.setProjectId(projectId); // enforce correct FK (important)

        boolean updated = service.updateSubProject(subproject);

        if (!updated) {
            return "redirect:/projects/" + projectId + "/subprojects?error=Could not update subproject";
        }

        return "redirect:/projects/" + projectId + "/subprojects?success=Subproject updated successfully";
    }

    ///  DELETE A SUBPROJECT BY ID (SCOPED TO THIS PROJECT) AND REDIRECT BACK
    @PostMapping("/{id}/delete")
    public String deleteSubproject(@PathVariable long projectId,
                                   @PathVariable long id) {

        boolean deleted = service.deleteSubProject(projectId, id);

        if (!deleted) {
            return "redirect:/projects/" + projectId + "/subprojects?error=Could not delete subproject";
        }

        return "redirect:/projects/" + projectId + "/subprojects?success=Subproject deleted successfully";
    }
}
