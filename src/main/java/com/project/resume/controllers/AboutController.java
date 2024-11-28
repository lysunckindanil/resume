package com.project.resume.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@SuppressWarnings("SameReturnValue")
@Controller
@Slf4j
public class AboutController {
    @GetMapping
    private String index(@RequestHeader(HttpHeaders.USER_AGENT) String userAgent) {
        log.info("About page opened, userAgent: {}", userAgent);
        return "about/about_page";
    }
}
