package com.project.document.dto;

import java.time.LocalDate;

public class LoanApplicationDTO {
    private Long loanId;
    private String loanType;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String maritalStatus;
    private String contactInfo;
    private String address;
    private Long annualSalary;
    private Long loanAmount;
    private Long customerId;
    private Long userId;

    // Default constructor
    public LoanApplicationDTO() {}

    // Getters and Setters
    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    // Add other getters and setters...

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
} 