package com.vatsalrajgor.eCommerce.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        // File name
        String name = file.getOriginalFilename();

        // Random name generate file
        String randomID = UUID.randomUUID().toString();
        String fileName1 = randomID.concat(name.substring(name.lastIndexOf(".")));

        // Fullpath
        String filePath = path + File.separator + fileName1;

        // Create a folder if not created
        File f = new File(path);
        if (!f.exists()) {
            f.mkdir();
        }

        // File copy
        Files.copy(file.getInputStream(), Paths.get(filePath));

        return fileName1;
    }
}
