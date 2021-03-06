package com.ptv.escort.Admin;


import com.ptv.escort.Utils.FileUploadUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileUploadUtil fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }


    public void storeFile(MultipartFile file, long id) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
            }


            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(id + "/" + fileName);
            File imageFile = targetLocation.toFile();

            if (!imageFile.exists()) {

                imageFile.getParentFile().mkdirs();
                imageFile.createNewFile();
                    }

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

//            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName, long id) {
        try {
            Path filePath = this.fileStorageLocation.resolve(id + "/" + fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found " + fileName, ex);
        }
    }


    public boolean deleteFile(String fileName,long id) {

        Path targetLocation = this.fileStorageLocation.resolve(id + "/" + fileName);
        System.out.println("target location " + targetLocation);
        Path dir = this.fileStorageLocation.resolve(Long.toString(id));
        File imageFile = targetLocation.toFile();
        boolean deleted = false;
        try {

            if (imageFile.delete()) {
                System.out.println(fileName + " is deleted!");
                FileUtils.deleteDirectory(new File(String.valueOf(dir)));
                deleted = true;
            } else {
                throw new RuntimeException("Sorry, unable to delete the escort. Please try again later!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deleted;
    }

}
