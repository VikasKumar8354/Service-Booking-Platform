package com.servicebooking.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadPath = Paths.get("uploads").toAbsolutePath();

    public String saveFile(MultipartFile file) {

        try {
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("No file received");
            }

            Files.createDirectories(uploadPath);

            String type = file.getContentType();

            // ✅ NULL SAFE MIME CHECK
            if (type == null ||
                    !(type.equalsIgnoreCase("application/pdf") ||
                            type.equalsIgnoreCase("image/jpeg") ||
                            type.equalsIgnoreCase("image/jpg") ||
                            type.equalsIgnoreCase("image/png"))) {

                throw new RuntimeException("Invalid file type: " + type);
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            file.transferTo(filePath.toFile());

            return filePath.toString();

        } catch (Exception exception) {
            exception.printStackTrace(); // ✅ show real error
            throw new RuntimeException("Upload failed: " + exception.getMessage());
        }
    }
}
