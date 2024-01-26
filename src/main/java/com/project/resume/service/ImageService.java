package com.project.resume.service;

import com.project.resume.model.Image;
import com.project.resume.repo.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    public void save(Image image) {
        imageRepository.save(image);
    }

    public List<Image> findAllImages() {
        return imageRepository.findAll();
    }

    public Optional<Image> findById(long id) {
        return imageRepository.findById(id);
    }

    public void deleteById(long id) {
        imageRepository.deleteById(id);
    }
}
