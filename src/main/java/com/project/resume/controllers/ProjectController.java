package com.project.resume.controllers;

import com.project.resume.model.Project;
import com.project.resume.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("SameReturnValue")
@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping()
    private String projects(Principal principal, Model model) {
        List<Project> projects = projectService.findAll(Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("main_projects", projects.stream().filter(Project::getMain).sorted().toList());
        model.addAttribute("other_projects", projects.stream().filter(x -> !x.getMain()).sorted().toList());
        model.addAttribute("user", principal == null ? "" : principal.getName());
        return "projects/projects";
    }

    @GetMapping("/{id}")
    private String project(@PathVariable("id") int id, Principal principal, Model model) {
        // if projects doesn't exist by id, then sends to error page
        if (projectService.findById(id).isPresent()) {
            Project project = projectService.findById(id).get();
            // Project fragment for thymeleaf with path and name
            // Fragment file name without extension should be the same as fragment name!

            model.addAttribute("fragment_name", projectService.getFragmentNameOfProject(project));
            model.addAttribute("fragment_path", projectService.getFragmentPathOfProject(project));
            model.addAttribute("project", project);
            model.addAttribute("user", principal == null ? "" : principal.getName());

            return "projects/project";
        }
        return "service/error";
    }

    @GetMapping("/add")
    private String addProject(Model model) {
        // returns project creation form with model attribute
        model.addAttribute("project", new Project());
        return "projects/new";
    }

    @PostMapping("/add")
    private String addProjectPost(@ModelAttribute Project project, @RequestParam(value = "page_file") MultipartFile page_file, @RequestParam(value = "image_file") MultipartFile image_file) {
        projectService.save(project, page_file, image_file);
        return "redirect:/projects/" + project.getId();
    }

    @GetMapping("/{id}/edit")
    private String editProject(@PathVariable("id") int id, Model model) {
        Optional<Project> project = projectService.findById(id);
        if (project.isPresent()) {
            model.addAttribute("project", project.get());
            return "projects/edit";
        }
        return "service/error";
    }

    @PostMapping("/{id}/edit")
    private String editProjectPost(@ModelAttribute Project project, @RequestParam(value = "page_file", required = false) MultipartFile page_file, @RequestParam(value = "image_file", required = false) MultipartFile image_file) {
        projectService.edit(project, page_file, image_file);
        return "redirect:/projects/" + project.getId();
    }

    @PostMapping("/{id}/delete")
    private String deleteProject(@PathVariable("id") int id) {
        projectService.deleteById(id);
        return "redirect:/projects";
    }

}
