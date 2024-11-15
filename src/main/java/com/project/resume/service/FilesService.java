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

@Service
@Slf4j
public class FilesService {
    @Value("${resource-path}")
    String resource_location_path;

    public String addFileToFolderStatic(MultipartFile file, Folder folder) {
        try {
            file.transferTo(Path.of(resource_location_path + folder.toString().toLowerCase() + File.separator + file.getOriginalFilename()));
        } catch (IOException e) {
            log.error(String.format("Unable to transfer %s to static folder %s", file.getOriginalFilename(), folder));
            log.error(e.toString());
        }
        return folder.toString().toLowerCase() + "/" + file.getOriginalFilename();
    }

    public void deleteFileFromStaticFolder(String file, Folder folder) {
        try {
            Files.delete(Path.of(resource_location_path + folder.toString().toLowerCase() + File.separator + file));
        } catch (IOException e) {
            log.error(String.format("Unable to delete %s from static folder %s", file, folder));
            log.error(e.toString());
        }
    }

    public String[] getListOfFilesFromStaticDir(Folder folder) {
        return new File(resource_location_path + folder.toString().toLowerCase()).list();
    }
}
