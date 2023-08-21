package com.project.resume.controllers;

import com.project.resume.model.Project;
import com.project.resume.repo.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProjectsController {
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectsController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @GetMapping("/projects")
    public String projects(Model model) {
        List<Project> projects = (List<Project>) projectRepository.findAll();
        model.addAttribute("main_projects", projects.stream().filter(Project::isMain).toList());
        model.addAttribute("other_projects", projects.stream().filter(x -> !x.isMain()).toList());
        model.addAttribute("active", "projects");
        return "projects/projects_page";
    }

    @GetMapping("/projects/{id}")
    public String project(@PathVariable("id") long id, Model model) {
        model.addAttribute("active", "projects");
        if (projectRepository.findById(id).isPresent()) {
            model.addAttribute("project", projectRepository.findById(id).get());
        } else {
            model.addAttribute("title", "No such project");
        }
        return "projects/project";
    }

}
