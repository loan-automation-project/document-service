package com.project.document.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.document.service.S3Service;

@RestController
public class DocumentController {

    @Autowired
    private S3Service s3Service;

    @PostMapping("/upload")
    public String uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            byte[] fileBytes = file.getBytes();
            return s3Service.uploadFile(fileName, fileBytes);
        } catch (Exception e) {
            return "Failed to upload file: " + e.getMessage();
        }
    }
    
    @GetMapping("/download/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadDocument(@PathVariable String fileName) {
        try {
            byte[] fileBytes = s3Service.downloadFile(fileName);

            // Create a ByteArrayResource from the file bytes
            ByteArrayResource resource = new ByteArrayResource(fileBytes);

            // Set headers for the response
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(fileBytes.length)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
