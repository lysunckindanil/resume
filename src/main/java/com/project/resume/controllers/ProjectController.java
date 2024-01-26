package com.project.resume.controllers;

import com.project.resume.model.Project;
import com.project.resume.service.FilesService;
import com.project.resume.service.ProjectService;
import com.project.resume.service.enums.Folder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("SameReturnValue")
@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;
    private final FilesService filesService;


    @GetMapping()
    private String projects(Principal principal, Model model) {
        List<Project> projects = projectService.findAll(Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("main_projects", projects.stream().filter(Project::getMain).sorted().toList());
        model.addAttribute("other_projects", projects.stream().filter(x -> !x.getMain()).sorted().toList());
        model.addAttribute("user", principal == null ? "" : principal.getName());
        return "projects/projects";
    }

    @GetMapping("/{id}")
    private String project(@PathVariable("id") int id, Principal principal, Model model) {
        // if projects doesn't exist by id, then sends to error page
        if (projectService.findById(id).isPresent()) {
            Project project = projectService.findById(id).get();
            // Project fragment for thymeleaf with path and name
            // Fragment file name without extension should be the same as fragment name!

            model.addAttribute("fragment_name", FilenameUtils.removeExtension(new File(project.getPage()).getName()));
            model.addAttribute("fragment_path", project.getPage());
            model.addAttribute("project", project);
            model.addAttribute("user", principal == null ? "" : principal.getName());

            return "projects/project";
        }
        return "service/error";
    }

    @GetMapping("/add")
    private String addProject(Model model) {
        // returns project creation form with model attribute
        model.addAttribute("project", new Project());
        return "projects/new";
    }

    @PostMapping("/add")
    private String addProjectPost(@ModelAttribute Project project, @RequestParam(value = "page_file") MultipartFile page_file, @RequestParam(value = "image_file") MultipartFile image_file) {

        // this method copies received file to PROJECT_PAGES
        project.setPage(filesService.addFileToFolderStatic(page_file, Folder.PROJECT_PAGES));
        project.setImage(filesService.addFileToFolderStatic(image_file, Folder.IMAGES));
        projectService.save(project);
        return "redirect:/projects/" + project.getId();
    }

    @GetMapping("/{id}/edit")
    private String editProject(@PathVariable("id") int id, Model model) {
        Optional<Project> project = projectService.findById(id);
        if (project.isPresent()) {
            model.addAttribute("project", project.get());
            return "projects/edit";
        }
        return "service/error";
    }

    @PostMapping("/{id}/edit")
    private String editProjectPost(@ModelAttribute Project project, @PathVariable("id") int id, @RequestParam(value = "page_file", required = false) MultipartFile page_file, @RequestParam(value = "image_file", required = false) MultipartFile image_file) {
        if (projectService.findById(id).isPresent()) {
            Project original_project = projectService.findById(id).get();

            // changes project's page html only if file was passed to the method
            if (!page_file.isEmpty()) {
                original_project.setPage(filesService.addFileToFolderStatic(page_file, Folder.PROJECT_PAGES));
            }

            // changes project's title image only if file was passed to the method
            if (!image_file.isEmpty()) {
                original_project.setImage(filesService.addFileToFolderStatic(image_file, Folder.IMAGES));
            }

            original_project.setTitle(project.getTitle());
            original_project.setDescription(project.getDescription());
            original_project.setMain(project.getMain());
            original_project.setOrder(project.getOrder());

            projectService.save(original_project);
            return "redirect:/projects/" + project.getId();
        }
        return "service/error";
    }

    @PostMapping("/{id}/delete")
    private String deleteProject(@PathVariable("id") int id) {
        projectService.deleteById(id);
        return "redirect:/projects";
    }

}
