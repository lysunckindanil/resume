package com.project.resume.service;

import com.project.resume.service.enums.Folder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Service
@Slf4j
public class FilesService {
    @Value("${images-path}")
    String images_path;
    @Value("${projects-path}")
    String projects_path;


    public String addFileToFolderStatic(MultipartFile file, Folder folder) {
        try {
            file.transferTo(Path.of(getPath(folder) + file.getOriginalFilename()));
        } catch (IOException e) {
            log.error(String.format("Unable to transfer %s to static folder %s", file.getOriginalFilename(), folder));
            log.error(e.toString());
        }
        return folder.toString().toLowerCase() + "/" + file.getOriginalFilename();
    }

    public void deleteFileFromStaticFolder(String file, Folder folder) {
        try {
            Files.delete(Path.of(getPath(folder) + File.separator + file));
        } catch (IOException e) {
            log.error(String.format("Unable to delete %s from static folder %s", file, folder));
            log.error(e.toString());
        }
    }

    public String[] getListOfFilesFromStaticDir(Folder folder) {
        return new File(Objects.requireNonNull(getPath(folder))).list();
    }

    private String getPath(Folder folder) {
        if (folder == Folder.IMAGES)
            return images_path;
        else if (folder == Folder.PROJECT_PAGES)
            return projects_path;
        return null;
    }
}
