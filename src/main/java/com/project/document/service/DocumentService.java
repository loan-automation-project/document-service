package com.project.document.service;

import com.project.document.client.LoanApplicationClient;
import com.project.document.dto.LoanApplicationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class DocumentService {

    @Autowired
    private S3Service s3Service;
    
    @Autowired
    private LoanApplicationClient loanApplicationClient;

    public String uploadDocument(MultipartFile file, String documentType) throws IOException {
        try {
            // Get the latest loan application for the user
            ResponseEntity<LoanApplicationDTO> loanResponse =
                loanApplicationClient.getLatestLoanApplication();
            
            if (!loanResponse.getStatusCode().is2xxSuccessful() || loanResponse.getBody() == null) {
                throw new RuntimeException("No active loan application found");
            }
            
            LoanApplicationDTO loanApplication = loanResponse.getBody();
            Long loanApplicationId = loanApplication.getLoanId();

            // Create S3 key with loan application ID
            String key = String.format("%d/%s/%s", 
                loanApplicationId, 
                documentType,
                file.getOriginalFilename()
            );
            
            return s3Service.uploadFile(
                key, 
                file.getBytes(), 
                file.getOriginalFilename(),
                documentType,
                loanApplicationId
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload document: " + e.getMessage(), e);
        }
    }
}
