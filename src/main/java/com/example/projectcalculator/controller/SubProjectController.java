package com.example.projectcalculator.controller;

import com.example.projectcalculator.model.SubProject;
import com.example.projectcalculator.service.SubProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/subprojects")
public class SubProjectController {

    private final SubProjectService subProjectService;

    @Autowired
    public SubProjectController(SubProjectService subProjectService) {
        this.subProjectService = subProjectService;
    }

    /**
     * #169 - GET /subprojects/[id]/edit
     * Show edit form with prefilled data
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<SubProject> subProjectOpt = subProjectService.getSubProjectById(id);

        if (subProjectOpt.isEmpty()) {
            return "redirect:/projects"; // or appropriate error page
        }

        model.addAttribute("subProject", subProjectOpt.get());
        return "subprojects/edit";
    }

    /**
     * #172 - POST /subprojects/[id]
     * Handle form submission for updating subproject
     */
    @PostMapping("/{id}")
    public String updateSubProject(
            @PathVariable Long id,
            @Valid @ModelAttribute("subProject") SubProject subProject,
            BindingResult bindingResult,
            Model model) {

        // Check if the ID in the path matches the object ID
        if (!id.equals(subProject.getId())) {
            return "redirect:/projects"; // or appropriate error handling
        }

        // Validate form inputs
        if (bindingResult.hasErrors()) {
            return "subprojects/edit";
        }

        // Perform update
        boolean updated = subProjectService.updateSubProject(subProject);

        if (!updated) {
            model.addAttribute("error", "Failed to update subproject. It may have been deleted or you don't have permission.");
            return "subprojects/edit";
        }

        return "redirect:/projects/" + subProject.getProjectId();
    }
}