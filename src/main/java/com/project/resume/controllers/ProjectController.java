package com.project.resume.controllers;

import com.project.resume.model.Project;
import com.project.resume.repo.ProjectRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
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
    public static final String PROJECT_FRAGMENT_PATH = "projects" + File.separator + "pages" + File.separator;
    private final ProjectRepository projectRepository;


    @GetMapping()
    private String projects(Principal principal, Model model) {
        List<Project> projects = projectRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("main_projects", projects.stream().filter(Project::isMain).sorted().toList());
        model.addAttribute("other_projects", projects.stream().filter(x -> !x.isMain()).sorted().toList());
        model.addAttribute("user", principal == null ? "" : principal.getName());
        return "projects/projects";
    }

    @GetMapping("/{id}")
    private String project(@PathVariable("id") int id, Principal principal, Model model) {
        // if projects doesn't exist by id, then sends to error page
        if (projectRepository.findById(id).isPresent()) {
            Project project = projectRepository.findById(id).get();
            // Project fragment for thymeleaf with path and name
            // Fragment file name without extension should be the same as fragment name!
            @Getter
            @Setter
            class Fragment {
                public String path;
                public String name;
            }
            Fragment fragment = new Fragment();
            fragment.setName(FilenameUtils.removeExtension(project.getPage()));
            fragment.setPath(PROJECT_FRAGMENT_PATH + fragment.name);

            model.addAttribute("fragment", fragment);
            model.addAttribute("project", project);
            model.addAttribute("user", principal == null ? "" : principal.getName());

            return "projects/project";
        }
        return "service/error";
    }

    @GetMapping("/add")
    String addProject(Model model) {
        // returns project creation form with model attribute
        model.addAttribute("project", new Project());
        return "projects/new";
    }

    @PostMapping("/add")
    private String addProjectPost(@ModelAttribute Project project, @RequestParam(value = "page_file") MultipartFile page_file, @RequestParam(value = "image_file") MultipartFile image_file) {

        try {
            // this method copies received file to PROJECT_FRAGMENT_PATH
            saveHtmlPage(page_file);
            project.setPage(page_file.getOriginalFilename());
        } catch (IOException e) {
            log.error("Unable to transfer html page");
        }

        try {
            image_file.transferTo(Path.of(ImageController.getImageStaticDir() + image_file.getOriginalFilename()));
            project.setImage(image_file.getOriginalFilename());
        } catch (IOException e) {
            log.error("Unable to add image file to static files dir");
            log.error(e.toString());
        }


        projectRepository.save(project);
        return "redirect:/projects/" + project.getId();
    }

    @GetMapping("/{id}/edit")
    private String editProject(@PathVariable("id") int id, Model model) {
        if (projectRepository.findById(id).isPresent()) {
            model.addAttribute("project", projectRepository.findById(id).get());
            return "projects/edit";
        }
        return "service/error";
    }

    @PostMapping("/{id}/edit")
    private String editProjectPost(@ModelAttribute Project project, @PathVariable("id") int id, @RequestParam(value = "page_file", required = false) MultipartFile page_file, @RequestParam(value = "image_file", required = false) MultipartFile image_file) {
        if (projectRepository.findById(id).isPresent()) {
            Project original_project = projectRepository.findById(id).get();

            // changes project's page html only if file was passed to the method
            if (!page_file.isEmpty()) {
                try {
                    saveHtmlPage(page_file);
                    original_project.setPage(page_file.getOriginalFilename());
                } catch (IOException e) {
                    log.error("Unable to transfer html page");
                }
            }

            // changes project's title image only if file was passed to the method
            if (!image_file.isEmpty()) {

                try {
                    image_file.transferTo(Path.of(ImageController.getImageStaticDir() + image_file.getOriginalFilename()));
                    original_project.setImage(image_file.getOriginalFilename());
                } catch (IOException e) {
                    log.error("Unable to add image file to static files dir");
                    log.error(e.toString());
                }
            }

            original_project.setTitle(project.getTitle());
            original_project.setDescription(project.getDescription());
            original_project.setMain(project.isMain());
            original_project.setOrder(project.getOrder());

            projectRepository.save(original_project);
            return "redirect:/projects/" + project.getId();
        }
        return "service/error";
    }

    @PostMapping("/{id}/delete")
    private String deleteProject(@PathVariable("id") int id) {
        projectRepository.deleteById(id);
        return "redirect:/projects";
    }

    private void saveHtmlPage(MultipartFile file) throws IOException {
        Path templates_dir = Paths.get("src" + File.separator + "main" + File.separator + "resources" + File.separator + "templates" + File.separator + ProjectController.PROJECT_FRAGMENT_PATH);
        Path filepath = Paths.get(templates_dir.toString(), file.getOriginalFilename());
        file.transferTo(filepath);
    }
}
