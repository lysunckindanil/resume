package com.project.resume.controllers;

import com.project.resume.model.Admin;
import com.project.resume.model.Project;
import com.project.resume.repo.ProjectRepository;
import com.project.resume.repo.UserRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    String addProjectPost(@ModelAttribute Project project, @RequestParam(value = "file", required = false) MultipartFile file) {
        Path dir = Paths.get("src/main/resources/templates/" + ProjectsController.PROJECT_CUSTOM_HTML);
        Path filepath = Paths.get(dir.toString(), file.getOriginalFilename());
        try {
            file.transferTo(filepath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        project.setFile_html(filepath.getFileName().toString());
        projectRepository.save(project);
        return "redirect:/projects";
    }
}
