package com.project.resume.controllers;

import com.project.resume.model.Image;
import com.project.resume.service.FilesService;
import com.project.resume.service.enums.Folder;
import com.project.resume.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/repository")
public class RepositoryController {
    private final FilesService filesService;
    private final ImageService imageService;

    @GetMapping()
    private String index(Model model) {
        model.addAttribute("image_list", imageService.findAllImages());
        model.addAttribute("static_image_list", filesService.getListOfFilesFromStaticDir(Folder.IMAGES));
        return "image/repository";
    }

    @GetMapping("/{id}")
    private ResponseEntity<?> getDatabaseImageById(@PathVariable Long id) {
        Optional<Image> optionalImage = imageService.findById(id);
        if (optionalImage.isPresent()) {
            Image image = optionalImage.get();
            return ResponseEntity.ok().header("fileName", image.getOriginalFileName()).contentType(MediaType.valueOf(image.getContentType())).contentLength(image.getSize()).body(new InputStreamResource(new ByteArrayInputStream(image.getBytes())));
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/delete")
    private String deleteDatabaseImagePost(@PathVariable long id) {
        imageService.deleteById(id);
        return "redirect:/repository";
    }

    @PostMapping("/add")
    private String addDatabaseImagePost(@RequestAttribute(value = "image_file") MultipartFile image_file, @RequestParam(value = "image_name") String name, @RequestParam(value = "image_description") String description) {
        Image image = toImageEntity(image_file);
        image.setName(name);
        image.setDescription(description);
        imageService.save(image);
        return "redirect:/repository";
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

    private static Image toImageEntity(MultipartFile file) {
        Image image = new Image();
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        try {
            image.setBytes(file.getBytes());
        } catch (IOException e) {
            log.error("Unable to convert image to entity");
            log.error(e.toString());
        }
        return image;
    }
}
