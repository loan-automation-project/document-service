package com.project.document.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.document.entity.DocumentEntity;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity , Long> {
	
	List<DocumentEntity> findByLoanId(int loanId);

}
