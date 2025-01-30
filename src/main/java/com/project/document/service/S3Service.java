package com.project.document.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.project.document.entity.DocumentEntity;
import com.project.document.repository.DocumentRepository;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class S3Service {
	
	@Autowired
	DocumentRepository documentRepository;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    private final S3Client s3Client;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(String key, byte[] file, String name, String type, Long loanId) {
        try {
            // Upload file to S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file));

            DocumentEntity document = new DocumentEntity();
            document.setDocumentName(name);
            document.setLoanId(loanId);
            document.setDocumentType(type);
            document.setDocumentStatus("Unverified");
            document.setS3key(key);
            documentRepository.saveAndFlush(document);

            return "File uploaded successfully: " + key;
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to upload file to S3: " + e.getMessage());
        }
    }

    
    public byte[] downloadFiles(Integer loanId) {
        try {
            List<DocumentEntity> documents = documentRepository.findByLoanId(loanId);

            if (documents.isEmpty()) {
                throw new RuntimeException("No documents found for loan ID: " + loanId);
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

            for (DocumentEntity doc : documents) {
                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(doc.getS3key())
                        .build();
                ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest);

                ZipEntry zipEntry = new ZipEntry(doc.getDocumentName());
                zipOutputStream.putNextEntry(zipEntry);
                response.transferTo(zipOutputStream);
                zipOutputStream.closeEntry();
            }

            zipOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException | S3Exception e) {
            throw new RuntimeException("Failed to create zip file: " + e.getMessage());
        }
    }
}