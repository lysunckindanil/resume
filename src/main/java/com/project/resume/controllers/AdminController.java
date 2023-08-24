package com.project.resume.controllers;

import com.project.resume.model.Project;
import com.project.resume.model.User;
import com.project.resume.repo.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@SuppressWarnings("SameReturnValue")
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final ProjectRepository projectRepository;

    @GetMapping
    public String index(@ModelAttribute User user, Model model) {
        List<Project> projects = (List<Project>) projectRepository.findAll();
        model.addAttribute("projects", projects.stream().sorted().toList());
        return "admin/index";
    }
}
