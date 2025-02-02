package com.project.document.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.cloud.openfeign.FeignClient;
import com.project.document.dto.LoanApplicationDTO;

@FeignClient(name = "loan-application")
public interface LoanApplicationClient {
//    @GetMapping("/api/loan-applications/{id}")
//    ResponseEntity<LoanApplicationDTO> getLoanApplication(@PathVariable("id") Long id);

    @GetMapping("/application/latest")
    ResponseEntity<LoanApplicationDTO> getLatestLoanApplication();
} 