package com.project.resume.controllers;

import com.project.resume.model.Project;
import com.project.resume.repo.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("SameReturnValue")
@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {
    public static final String PROJECT_CUSTOM_HTML = "projects/pages/";
    private final ProjectRepository projectRepository;


    @GetMapping()
    public String projects(Model model) {
        List<Project> projects = (List<Project>) projectRepository.findAll();
        model.addAttribute("main_projects", projects.stream().filter(Project::isMain).sorted().toList());
        model.addAttribute("other_projects", projects.stream().filter(x -> !x.isMain()).sorted().toList());
        return "projects/projects";
    }

    @GetMapping("/{id}")
    public String project(@PathVariable("id") int id, Model model) {
        if (projectRepository.findById(id).isPresent()) {
            Project project = projectRepository.findById(id).get();
            model.addAttribute("fragment", PROJECT_CUSTOM_HTML + project.getFile_html().replace(".html", ""));
            model.addAttribute("project", project);
            return "projects/project";
        }
        return null;
    }

    @GetMapping("/add")
    String addProject(Model model) {
        model.addAttribute("project", new Project());
        return "projects/new_form";
    }

    @PostMapping("/add")
    String addProjectPost(@ModelAttribute Project project, @RequestParam(value = "file", required = false) MultipartFile file) {
        Path dir = Paths.get("src/main/resources/templates/" + ProjectController.PROJECT_CUSTOM_HTML);
        Path filepath = Paths.get(dir.toString(), file.getOriginalFilename());
        try {
            file.transferTo(filepath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        project.setFile_html(filepath.getFileName().toString());
        projectRepository.save(project);
        return "redirect:/admin";
    }

    @GetMapping("/{id}/edit")
    String editProject(@PathVariable("id") int id, Model model) {
        model.addAttribute("project", projectRepository.findById(id).orElse(null));
        return "projects/edit_form";
    }

    @PostMapping("/{id}/edit")
    String editProjectPost(@ModelAttribute Project project, @PathVariable("id") int id, @RequestParam(value = "file", required = false) MultipartFile file) {
        if (projectRepository.findById(id).isPresent()) {
            if (!Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
                addProjectPost(project, file);
            } else {
                Project original_project = projectRepository.findById(id).get();
                project.setFile_html(original_project.getFile_html());
                projectRepository.save(project);
            }
        }
        return "redirect:/admin";
    }

    @PostMapping("/{id}/delete")
    String deleteProject(@PathVariable("id") int id) {
        projectRepository.deleteById(id);
        return "redirect:/admin";
    }

}
