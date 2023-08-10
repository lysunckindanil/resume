package com.project.resume.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ResourcesController {
    @GetMapping("/resources")
    public String resources(Model model) {
        model.addAttribute("active", "resources");
        return "resources/resources_page";
    }
}
