package com.project.resume.controllers;

import com.project.resume.model.Admin;
import com.project.resume.model.Project;
import com.project.resume.repo.ProjectRepository;
import com.project.resume.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    public AdminController(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String authorization(Model model) {
        model.addAttribute("admin", new Admin());
        return "admin/authorization";
    }

    @PostMapping
    public String index(@ModelAttribute Admin admin) {
        List<Admin> adminList = (List<Admin>) userRepository.findAll();
        if (adminList.stream().anyMatch(x -> x.equals(admin))) return "admin/admin_page";
        else return "redirect:/admin";
    }

    @GetMapping("add")
    String addProject(Model model) {
        model.addAttribute("project", new Project());
        return "admin/project_form";
    }

    @PostMapping("add")
    String addProjectPost(@ModelAttribute Project project) {
        projectRepository.save(project);
        return "redirect:/projects";
    }
}
