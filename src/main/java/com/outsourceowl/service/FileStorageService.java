package com.outsourceowl.service;

import com.google.common.collect.ImmutableSet;
import com.outsourceowl.config.FileStorageProperties;
import com.outsourceowl.exception.FileStorageException;
import com.outsourceowl.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Set;

import static com.google.common.io.ByteStreams.toByteArray;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@Service
public class FileStorageService {

  private static final Set<String> ALLOWED_IMAGE_FORMATS =
      ImmutableSet.of(IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE);

  private final Path fileStorageLocation;

  @Autowired
  public FileStorageService(FileStorageProperties fileStorageProperties) {
    this.fileStorageLocation =
        Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

    try {
      Files.createDirectories(this.fileStorageLocation);
    } catch (Exception ex) {
      throw new FileStorageException(
          "Could not create the directory where the uploaded files will be stored.", ex);
    }
  }

  public String storeFile(MultipartFile file, String fileName) {
    try {
      String fileType =
          Optional.ofNullable(file.getContentType())
              .map(MediaType::parseMediaType)
              .filter(ft -> ALLOWED_IMAGE_FORMATS.contains(ft.toString()))
              .map(MimeType::getSubtype)
              .orElseThrow(
                  () -> new FileStorageException("File type now allowed " + file.getContentType()));

      Path targetLocation = this.fileStorageLocation.resolve(fileName + "." + fileType);
      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

      return targetLocation.getFileName().toString();
    } catch (IOException ex) {
      throw new FileStorageException(
          "Could not store file " + fileName + ". Please try again!", ex);
    }
  }

  public byte[] loadFileAsResource(String fileName) {
    try {
      Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
      Resource resource = new UrlResource(filePath.toUri());
      if (resource.exists()) {
        return toByteArray(resource.getInputStream());
      }
    } catch (IOException e) {
      throw new ResourceNotFoundException("File not found " + fileName, e);
    }

    throw new ResourceNotFoundException("File not found " + fileName);
  }
}
