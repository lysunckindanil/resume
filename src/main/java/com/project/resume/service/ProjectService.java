package com.project.resume.service;

import com.project.resume.model.Project;
import com.project.resume.repo.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    public void save(Project project) {
        projectRepository.save(project);
    }

    public Optional<Project> findById(Integer id) {
        return projectRepository.findById(id);
    }

    public void deleteById(int id) {
        projectRepository.deleteById(id);
    }

    public List<Project> findAll(Sort sort) {
        return projectRepository.findAll(sort);
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }
}
