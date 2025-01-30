package com.project.document.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.document.service.S3Service;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("loanId") Long loanId) {

        try {
            String key = loanId + "/" + file.getOriginalFilename(); 
            String message = s3Service.uploadFile(key, file.getBytes(), name, type, loanId);
            return ResponseEntity.ok(message);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }
    
    @GetMapping("/download/{loanId}")
    public ResponseEntity<byte[]> downloadFilesAsZip(@PathVariable Integer loanId) {
        byte[] zipData = s3Service.downloadFiles(loanId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment().filename("loan_" + loanId + "_documents.zip").build());

        return new ResponseEntity<>(zipData, headers, HttpStatus.OK);
    }
}
