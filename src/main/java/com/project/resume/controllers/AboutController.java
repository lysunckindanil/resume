package com.project.resume.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@SuppressWarnings("SameReturnValue")
@Controller
public class AboutController {
    @GetMapping
    public String index() {
        return "about/about_page";
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
