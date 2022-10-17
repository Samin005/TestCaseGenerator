package com.example.testcasegenerator.repository;

import com.example.testcasegenerator.model.Loan;
import org.springframework.data.repository.CrudRepository;

public interface LoanRepository extends CrudRepository<Loan, Integer> {
}
