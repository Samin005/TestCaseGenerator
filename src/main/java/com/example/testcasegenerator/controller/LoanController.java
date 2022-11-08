package com.example.testcasegenerator.controller;

import com.example.testcasegenerator.model.Loan;
import com.example.testcasegenerator.repository.LoanRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @RequestMapping(value = "/{loanId}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<Object> updateLoan (@PathVariable int loanId, @RequestBody Loan updatedLoan) {
        if(loanRepository.findById(loanId).isPresent()){
            return new ResponseEntity<>(loanRepository.save(updatedLoan), HttpStatus.OK);
        }
        else return new ResponseEntity<>("No loan found with ID: " + loanId, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{loanId}")
    public ResponseEntity<Object> deleteLoanById(@PathVariable int loanId) {
        if(loanRepository.findById(loanId).isPresent()){
            loanRepository.deleteById(loanId);
            return new ResponseEntity<>("Successfully deleted loan", HttpStatus.OK);
        }
        else return new ResponseEntity<>("No loan found with ID: " + loanId, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Object> deleteAllUsers() {
        loanRepository.deleteAll();
        return new ResponseEntity<>("Successfully deleted all loans", HttpStatus.OK);
    }
}
