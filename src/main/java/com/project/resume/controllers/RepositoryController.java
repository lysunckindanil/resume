package com.project.resume.controllers;

import com.project.resume.service.FilesService;
import com.project.resume.service.enums.Folder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/repository")
public class RepositoryController {
    private final FilesService filesService;

    @GetMapping()
    private String index(Model model) {
        model.addAttribute("static_image_list", filesService.getListOfFilesFromStaticDir(Folder.IMAGES));
        return "image/repository";
    }

    @PostMapping("/add-static")
    private String addStaticImagePost(@RequestAttribute(value = "image_file", required = false) MultipartFile image_file) {
        filesService.addFileToFolderStatic(image_file, Folder.IMAGES);
        return "redirect:/repository";
    }

    @PostMapping("/{image}/delete-static")
    private String deleteStaticImagePost(@PathVariable String image) {
        filesService.deleteFileFromStaticFolder(image, Folder.IMAGES);
        return "redirect:/repository";
    }
}
