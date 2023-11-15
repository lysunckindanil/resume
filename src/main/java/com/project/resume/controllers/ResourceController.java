package com.project.resume.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@SuppressWarnings("SameReturnValue")
@Controller
public class ResourceController {
    @GetMapping("/resources")
    public String resources() {
        return "templates/resources/resources_page";
    }
}
