package com.project.resume.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@SuppressWarnings("SameReturnValue")
@Controller
public class ResourceController {
    @GetMapping("/resources")
    public String resources(Model model) {
        return "resources/resources_page";
    }
}
