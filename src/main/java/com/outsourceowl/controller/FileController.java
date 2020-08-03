package com.outsourceowl.controller;

import com.outsourceowl.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
public class FileController {

  private final FileStorageService fileStorageService;

  @Autowired
  public FileController(FileStorageService fileStorageService) {
    this.fileStorageService = fileStorageService;
  }

  @GetMapping(
      value = "/images/{fileName:.+}",
      produces = {IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE})
  public byte[] downloadFile(@PathVariable String fileName) {
    return fileStorageService.loadFileAsResource(fileName);
  }
}
