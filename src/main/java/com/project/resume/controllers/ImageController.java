package com.project.resume.controllers;

import com.project.resume.model.Image;
import com.project.resume.repo.ImageRepository;
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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/repository")
public class ImageController {
    private final ImageRepository imageRepository;
    private static final String STATIC_DIR = File.separator + "opt" + File.separator + "resume" + File.separator + "images";

    @GetMapping()
    private String index(Model model) {
        model.addAttribute("image_list", imageRepository.findAll());
        model.addAttribute("static_image_list", getStaticImages());
        return "image/repository";
    }

    @GetMapping("/{id}")
    private ResponseEntity<?> getDatabaseImageById(@PathVariable Long id) {
        Image image = imageRepository.findById(id).orElse(null);
        assert image != null;
        return ResponseEntity.ok().header("fileName", image.getOriginalFileName()).contentType(MediaType.valueOf(image.getContentType())).contentLength(image.getSize()).body(new InputStreamResource(new ByteArrayInputStream(image.getBytes())));
    }

    @GetMapping("/name/{name}")
    private ResponseEntity<?> getDatabaseImageByName(@PathVariable String name) {
        Optional<Image> optionalImage = imageRepository.findAll().stream().filter(x -> x.getName() != null).filter(x -> x.getName().equals(name)).findAny();
        if (optionalImage.isPresent()) {
            Image image = optionalImage.get();
            return ResponseEntity.ok().header("fileName", image.getOriginalFileName()).contentType(MediaType.valueOf(image.getContentType())).contentLength(image.getSize()).body(new InputStreamResource(new ByteArrayInputStream(image.getBytes())));
        } else {
            return ResponseEntity.badRequest().body(new Object());
        }
    }

    @PostMapping("/{id}/delete")
    private String deleteDatabaseImagePost(@PathVariable long id) {
        imageRepository.deleteById(id);
        return "redirect:/repository";
    }

    @PostMapping("/add")
    private String addDatabaseImagePost(@RequestAttribute(value = "image_file") MultipartFile image_file, @RequestParam(value = "image_name") String name, @RequestParam(value = "image_description") String description) {
        try {
            Image image = toImageEntity(image_file);
            image.setName(name);
            image.setDescription(description);
            imageRepository.save(image);
        } catch (IOException e) {
            log.error("Unable to add image file to repository");
            log.error(e.toString());
        }
        return "redirect:/repository";
    }

    @PostMapping("/add-static")
    private String addStaticImagePost(@RequestAttribute(value = "image_file", required = false) MultipartFile image_file) {
        try {
            image_file.transferTo(Path.of(STATIC_DIR + File.separator + image_file.getOriginalFilename()));
        } catch (IOException e) {
            log.error("Unable to add image file to static files dir");
            log.error(e.toString());
        }
        return "redirect:/repository";
    }

    @PostMapping("/{image}/delete-static")
    private String deleteStaticImagePost(@PathVariable String image) {
        try {
            Files.delete(Path.of(STATIC_DIR + File.separator + image));
        } catch (IOException e) {
            log.error("Unable to delete image in static files dir");
            log.error(e.toString());
        }
        return "redirect:/repository";
    }


    public static List<String> getStaticImages() {
        File[] file = new File(STATIC_DIR).listFiles();
        return file == null ? new ArrayList<>() : Arrays.stream(file).map(File::getName).toList();
    }

    public static Image toImageEntity(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes());
        return image;
    }

    public static String getImageStaticDir() {
        return STATIC_DIR + File.separator;
    }

}
