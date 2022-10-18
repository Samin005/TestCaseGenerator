package com.example.testcasegenerator.cucumber;

import com.example.testcasegenerator.model.*;
import io.cucumber.java.en.*;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;
import com.example.testcasegenerator.repository.LoanRepository;

public class ManualLoanStepDefinitions {
  @Autowired
  private LoanRepository loanRepository;
  private Loan currentLoan = new Loan();

  @Given("delete existing loans")
  public void deleteExistingLoans() {
      loanRepository.deleteAll();
      assertEquals(0, loanRepository.count());
      System.out.println("Deleted all loans");
  }

  @Given("loan properties are {int} {int} {int}")
  public void setLoanProperties(int id, int user_id, int item_id) {
      currentLoan.setId(id);
      if(user_id != -1) {
          User user = new User();
          user.setId(user_id);
          currentLoan.setUser(user);
      }
      if(item_id != -1) {
          Item item = new Item();
          item.setId(item_id);
          currentLoan.setItem(item);
      }
  }

  @When("create current loan")
  public void createCurrentLoan() {
      try{
          loanRepository.save(currentLoan);
          System.out.println("Saved loan with id: " + currentLoan.getId());
      } catch (Exception e) {
          e.printStackTrace();
      }
  }

  private Loan getLoanById(int id) {
      if(loanRepository.findById(id).isPresent())
          return loanRepository.findById(id).get();
      else return null;
  }

  @Then("create single loan status should be {string}")
  public void checkSingleLoanCreateStatus(String status) {
      Loan createdLoan = getLoanById(currentLoan.getId());
      if(status.equals("valid")) {
          assertEquals(1, loanRepository.count());
          assertTrue(new ReflectionEquals(createdLoan, new String[] {"user", "item"}).matches(currentLoan));
          assertTrue(new ReflectionEquals(createdLoan.getUser().getId()).matches(currentLoan.getUser().getId()));
          assertTrue(new ReflectionEquals(createdLoan.getItem().getId()).matches(currentLoan.getItem().getId()));
      }
      else {
          assertNotEquals(1, loanRepository.count());
          assertNull(createdLoan);
      }
  }

  @Then("create loan status should be {string}")
  public void checkLoanCreateStatus(String status) {
      Loan createdLoan = getLoanById(currentLoan.getId());
      if(status.equals("valid")) {
          assertTrue(new ReflectionEquals(createdLoan, new String[] {"user", "item"}).matches(currentLoan));
          assertTrue(new ReflectionEquals(createdLoan.getUser().getId()).matches(currentLoan.getUser().getId()));
          assertTrue(new ReflectionEquals(createdLoan.getItem().getId()).matches(currentLoan.getItem().getId()));
      }
      else assertFalse(new ReflectionEquals(createdLoan).matches(currentLoan));
  }

  @Then("fetching loan {int} should be {string}")
  public void fetchingLoanStatus(int id, String status) {
      Loan fetchedLoan = getLoanById(id);
      if(status.equals("valid")) {
          assertTrue(new ReflectionEquals(fetchedLoan, new String[] {"user", "item"}).matches(currentLoan));
          assertTrue(new ReflectionEquals(fetchedLoan.getUser().getId()).matches(currentLoan.getUser().getId()));
          assertTrue(new ReflectionEquals(fetchedLoan.getItem().getId()).matches(currentLoan.getItem().getId()));
      }
      else assertNull(fetchedLoan);
  }

  @Then("deleting loan {int} should be {string}")
  public void deletingLoanStatus(int id, String status) {
      try{
          loanRepository.deleteById(id);
          assertEquals("valid", status);
      } catch (Exception e) {
          e.printStackTrace();
          assertEquals("invalid", status);
      }
  }

}