package com.project.resume.controllers;

import com.project.resume.model.Admin;
import com.project.resume.model.Project;
import com.project.resume.repo.ProjectRepository;
import com.project.resume.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

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
    public String index(@ModelAttribute Admin admin, Model model) {
        List<Admin> adminList = (List<Admin>) userRepository.findAll();
        if (adminList.stream().anyMatch(x -> x.equals(admin))) {
            model.addAttribute("projects", projectRepository.findAll());
            return "admin/admin_page";
        } else return "redirect:/admin";
    }

    //Projects

    @GetMapping("projects/add")
    String addProject(Model model) {
        model.addAttribute("project", new Project());
        return "admin/project_form";
    }

    @PostMapping("projects/add")
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
        return "redirect:/admin";
    }

    @GetMapping("projects/{id}/edit")
    String editProject(@PathVariable("id") int id, Model model) {
        model.addAttribute("project", projectRepository.findById(id).orElse(null));
        return "admin/project_edit_form";
    }

    @PostMapping("projects/{id}/edit")
    String editProjectPost(@ModelAttribute Project project, @PathVariable("id") int id, @RequestParam(value = "file", required = false) MultipartFile file) {
        if (projectRepository.findById(id).isPresent()) {
            Project original_project = projectRepository.findById(id).get();
            original_project.setTitle(project.getTitle());
            original_project.setDescription(project.getDescription());
            original_project.setMain(project.isMain());
            original_project.setImg(project.getImg());
            if (!Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
                addProjectPost(original_project, file);
            } else {
                projectRepository.save(original_project);
            }
        }

        return "redirect:/admin";
    }

    @PostMapping("projects/{id}/delete")
    String deleteProject(@PathVariable("id") int id) {
        projectRepository.deleteById(id);
        return "redirect:/admin";
    }

}
