package com.example.testcasegenerator.controller;

import com.example.testcasegenerator.model.Loan;
import com.example.testcasegenerator.repository.LoanRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/loan")
public class LoanController {
    private final LoanRepository loanRepository;

    public LoanController(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @GetMapping
    public Iterable<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    @GetMapping(path = "/{loanId}")
    public Loan getLoanById(@PathVariable int loanId) {
        return loanRepository.findById(loanId).get();
    }

    @PostMapping
    public Loan addNewLoan(@RequestBody Loan newLoan) {
        return loanRepository.save(newLoan);
    }
}
