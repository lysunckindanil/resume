package com.project.resume.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@SuppressWarnings("SameReturnValue")
@Controller
@Slf4j
public class AboutController {
    @GetMapping
    private String index() {
        log.info("About page opened");
        return "about/about_page";
    }
}
