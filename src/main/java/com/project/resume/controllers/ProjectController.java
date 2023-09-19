package com.project.resume.controllers;

import com.project.resume.model.Image;
import com.project.resume.model.Project;
import com.project.resume.repo.ProjectRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

@SuppressWarnings("SameReturnValue")
@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {
    public static final String PROJECT_CUSTOM_HTML = "projects/pages/";
    private final ProjectRepository projectRepository;


    @GetMapping()
    private String projects(Principal principal, Model model) {
        List<Project> projects = projectRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("main_projects", projects.stream().filter(Project::isMain).toList());
        model.addAttribute("other_projects", projects.stream().filter(x -> !x.isMain()).toList());
        model.addAttribute("user", principal == null ? "" : principal.getName());
        return "projects/projects";
    }

    @GetMapping("/{id}")
    private String project(@PathVariable("id") int id, Principal principal, Model model) {
        if (projectRepository.findById(id).isPresent()) {
            Project project = projectRepository.findById(id).get();
            @Getter
            @Setter
            class Fragment {
                public String path;
                public String name;
            }
            Fragment fragment = new Fragment();
            fragment.setName(project.getPage().replace(".html", ""));
            fragment.setPath(PROJECT_CUSTOM_HTML + fragment.name);
            model.addAttribute("fragment", fragment);
            model.addAttribute("project", project);
            model.addAttribute("user", principal == null ? "" : principal.getName());

            return "projects/project";
        }
        return "projects/blank";
    }

    @GetMapping("/add")
    String addProject(Model model) {
        model.addAttribute("project", new Project());
        return "projects/new";
    }

    @PostMapping("/add")
    private String addProjectPost(@ModelAttribute Project project, @RequestParam(value = "page_file", required = false) MultipartFile page_file, @RequestParam(value = "image_file", required = false) MultipartFile image_file) {
        System.out.println(page_file.isEmpty());
        if (!page_file.isEmpty()) {
            try {
                saveHtmlPage(page_file);
                project.setPage(page_file.getName());
            } catch (IOException e) {
                log.error("Unable to transfer html page");
            }
        } else if (project.getPage() == null) project.setPage("blank.html");


        if (!image_file.isEmpty()) {
            try {
                Image image = ImageController.toImageEntity(image_file);
                project.setImage(image);
            } catch (IOException e) {
                log.error("Unable to add image file");
            }
        }

        projectRepository.save(project);
        return "redirect:/projects/" + project.getId();
    }


    @GetMapping("/{id}/edit")
    private String editProject(@PathVariable("id") int id, Model model) {
        model.addAttribute("project", projectRepository.findById(id).orElse(null));
        return "projects/edit";
    }

    @PostMapping("/{id}/edit")
    private String editProjectPost(@ModelAttribute Project project, @PathVariable("id") int id, @RequestParam(value = "page_file", required = false) MultipartFile page_file, @RequestParam(value = "image_file", required = false) MultipartFile image_file) {
        addProjectPost(project, page_file, image_file);
        return "redirect:/projects/" + id;
    }

    @PostMapping("/{id}/delete")
    private String deleteProject(@PathVariable("id") int id) {
        projectRepository.deleteById(id);
        return "redirect:/projects";
    }


    private void saveHtmlPage(MultipartFile file) throws IOException {
        Path templates_dir = Paths.get("src" + File.separator + "main" + File.separator + "resources" + File.separator + "templates" + File.separator + ProjectController.PROJECT_CUSTOM_HTML);
        Path filepath = Paths.get(templates_dir.toString(), file.getOriginalFilename());
        file.transferTo(filepath);
    }
}
