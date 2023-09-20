package com.project.resume.controllers;

import com.project.resume.model.Image;
import com.project.resume.repo.ImageRepository;
import com.project.resume.repo.ProjectRepository;
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
@RequestMapping("/images")
@Slf4j
public class ImageController {
    private final ImageRepository imageRepository;
    private final ProjectRepository projectRepository;

    @GetMapping("/{id}")
    private ResponseEntity<?> getImageById(@PathVariable Long id) {
        Image image = imageRepository.findById(id).orElse(null);
        assert image != null;
        return ResponseEntity.ok().header("fileName", image.getOriginalFileName()).contentType(MediaType.valueOf(image.getContentType())).contentLength(image.getSize()).body(new InputStreamResource(new ByteArrayInputStream(image.getBytes())));
    }

    @GetMapping("/name/{name}")
    private ResponseEntity<?> getImageByName(@PathVariable String name) {
        Optional<Image> optionalImage = imageRepository.findAll().stream().filter(x -> x.getName() != null).filter(x -> x.getName().equals(name)).findAny();
        if (optionalImage.isPresent()) {
            Image image = optionalImage.get();
            return ResponseEntity.ok().header("fileName", image.getOriginalFileName()).contentType(MediaType.valueOf(image.getContentType())).contentLength(image.getSize()).body(new InputStreamResource(new ByteArrayInputStream(image.getBytes())));
        } else {
            return ResponseEntity.badRequest().body(new Object());
        }
    }

    @GetMapping("/add")
    private String addImage(Model model) {
        model.addAttribute("image_list", imageRepository.findAll());
        model.addAttribute("project_list", projectRepository.findAll());
        return "image/add";
    }

    @PostMapping("/add")
    private String addImagePost(@RequestAttribute(value = "image_file") MultipartFile image_file, @RequestParam(value = "image_name") String name) {
        try {
            Image image = toImageEntity(image_file);
            image.setName(name);
            imageRepository.save(image);
        } catch (IOException e) {
            log.error("Unable to add image file to repository");
        }
        return "redirect:/";
    }

    @PostMapping("{id}/delete")
    private String deleteImagePost(@PathVariable long id) {
        if (projectRepository.findAll().stream().noneMatch(x -> x.getImage().getId() == id))
            imageRepository.deleteById(id);
        return "redirect:/images/add";
    }

    public static Image toImageEntity(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes());
        return image;
    }

}
