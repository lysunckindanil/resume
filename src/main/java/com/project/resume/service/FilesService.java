package com.project.resume.service;

import com.project.resume.service.enums.Folder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
public class FilesService {
    public static final String STATIC_DIR = File.separator + "opt" + File.separator + "resume" + File.separator;

    public String addFileToFolderStatic(MultipartFile file, Folder folder) {
        try {
            file.transferTo(Path.of(STATIC_DIR + folder.toString().toLowerCase() + File.separator + file.getOriginalFilename()));
        } catch (IOException e) {
            log.error(String.format("Unable to trasfer %s to static folder %s", file.getOriginalFilename(), folder));
            log.error(e.toString());
        }
        return folder.toString().toLowerCase() + "/" + file.getOriginalFilename();
    }

    public void deleteFileFromStaticFolder(String file, Folder folder) {
        try {
            Files.delete(Path.of(STATIC_DIR + folder.toString().toLowerCase() + File.separator + file));
        } catch (IOException e) {
            log.error(String.format("Unable to delet %s from static folder %s", file, folder));
            log.error(e.toString());
        }
    }

    public String[] getListOfFilesFromStaticDir(Folder folder) {
        return new File(STATIC_DIR + folder.toString().toLowerCase()).list();
    }
}
