package com.project.resume.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@SuppressWarnings("SameReturnValue")
@Controller
public class AboutController {
    @GetMapping
    private String index() {
        return "about/about_page";
    }
}
