package com.example.testcasegenerator.cucumber;

import io.cucumber.java.en.*;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;
import java.util.*;
import com.example.testcasegenerator.model.*;
import com.example.testcasegenerator.repository.*;

public class LoanStepDefinitions {
  @Autowired
  private ItemRepository itemRepository;
  private Iterable<Item> itemSnapshot;
  @Autowired
  private LoanRepository loanRepository;
  private Iterable<Loan> loanSnapshot;
  @Autowired
  private TitleRepository titleRepository;
  private Iterable<Title> titleSnapshot;
  @Autowired
  private UserRepository userRepository;
  private Iterable<User> userSnapshot;
  private Loan currentLoan = new Loan();

  @Given("delete existing loans")
  public void deleteExistingLoans() {
      loanRepository.deleteAll();
      assertEquals(0, loanRepository.count());
      System.out.println("Deleted all loans");
  }

  @Given("create loan with values {int} {int} {int} {int} {int}")
  public void setLoanProperties(int id, int user_id, int item_id, int renewal, int created_due_interval) {
      currentLoan = new Loan(created_due_interval);
      currentLoan.setId(id);
      if(user_id != -1) 
          currentLoan.setUser(userRepository.findById(user_id).get());
      if(item_id != -1) 
          currentLoan.setItem(itemRepository.findById(item_id).get());
      currentLoan.setRenewal(renewal);
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

  @Given("save database snapshot for loans and rest of the world")
  public void saveDatabaseSnapshot() {
      itemSnapshot = itemRepository.findAll();
      loanSnapshot = loanRepository.findAll();
      titleSnapshot = titleRepository.findAll();
      userSnapshot = userRepository.findAll();
  }

  private void assertEntityEquals(Iterable expectedEntityIterable, Iterable actualEntityIterable, String[] foreignKeys) {
      Object[] expectedEntity = new ArrayList((Collection) expectedEntityIterable).toArray();
      Object[] actualEntity = new ArrayList((Collection) actualEntityIterable).toArray();
      assertEquals(expectedEntity.length, actualEntity.length);
      for(int i = 0; i < expectedEntity.length; i++)
          assertTrue(new ReflectionEquals(expectedEntity[i], foreignKeys).matches(actualEntity[i]));
  }

  private void assertRemainingEntries(Iterable expectedEntriesIterable, Iterable actualEntriesIterable, String[] foreignKeys) {
      Object[] expectedEntries = new ArrayList((Collection) expectedEntriesIterable).toArray();
      Object[] actualEntries = new ArrayList((Collection) expectedEntriesIterable).toArray();
      if(expectedEntries.length == actualEntries.length) 
          for(int i = 0; i < expectedEntries.length; i++) 
              assertTrue(new ReflectionEquals(expectedEntries[i], foreignKeys).matches(actualEntries[i]));
      else {
          ArrayList actualEntriesList = new ArrayList((Collection) actualEntriesIterable);
          actualEntriesList.remove(actualEntriesList.size() - 1);
          Object[] actualEntriesWithoutLatest = actualEntriesList.toArray();
          assertEquals(expectedEntries.length, actualEntriesWithoutLatest.length);
          for(int i = 0; i < expectedEntries.length; i++)
              assertTrue(new ReflectionEquals(expectedEntries[i], foreignKeys).matches(actualEntriesWithoutLatest[i]));
      }
  }

  private void assertLoanObjectEquals(Loan loan) {
      assertTrue(new ReflectionEquals(loan, new String[]{"user", "item", "created", "due"}).matches(currentLoan));
      assertTrue(new ReflectionEquals(loan.getUser().getId()).matches(currentLoan.getUser().getId()));
      assertTrue(new ReflectionEquals(loan.getItem().getId()).matches(currentLoan.getItem().getId()));
      assertTrue(new ReflectionEquals(loan.getCreated()).matches(currentLoan.getCreated()));
      assertTrue(new ReflectionEquals(loan.getDue()).matches(currentLoan.getDue()));
  }

  private void assertOtherEntitiesUnchanged() {
      assertEntityEquals(itemSnapshot, itemRepository.findAll(), new String[]{"title"});
      assertEntityEquals(titleSnapshot, titleRepository.findAll(), null);
      assertEntityEquals(userSnapshot, userRepository.findAll(), null);
  }

  private void assertCreationStatusWithSnapshotValidation(Loan createdLoan) {
      assertLoanObjectEquals(createdLoan);
      assertRemainingEntries(loanSnapshot, loanRepository.findAll(), new String[]{"user", "item"});

      assertOtherEntitiesUnchanged();
  }

  private void assertSnapshotUnchanged() {
      assertEntityEquals(loanSnapshot, loanRepository.findAll(), new String[]{"user", "item"});
      assertOtherEntitiesUnchanged();
  }

  @Then("create single loan status should be {string} with snapshot validation")
  public void checkSingleLoanCreateStatus(String status) {
      Loan createdLoan = getLoanById(currentLoan.getId());
      if(status.equals("valid")) {
          assertEquals(1, loanRepository.count());
          assertCreationStatusWithSnapshotValidation(createdLoan);
      }
      else {
          assertNotEquals(1, loanRepository.count());
          assertSnapshotUnchanged();
          assertNull(createdLoan);
      }
  }

  @Then("create loan status should be {string} with snapshot validation")
  public void checkLoanCreateStatus(String status) {
      Loan createdLoan = getLoanById(currentLoan.getId());
      if(status.equals("valid")) {
          assertCreationStatusWithSnapshotValidation(createdLoan);
      }
      else assertSnapshotUnchanged();
  }

  @Then("fetching loan {int} should be {string} with snapshot validation")
  public void fetchingLoanStatus(int id, String status) {
      Loan fetchedLoan = getLoanById(id);
      if(status.equals("valid")) {
          assertLoanObjectEquals(fetchedLoan);
      }
      else assertNull(fetchedLoan);
      assertSnapshotUnchanged();
  }

  private void assertDeletionStatusWithSnapshotValidation() {
      assertRemainingEntries(loanRepository.findAll(), loanSnapshot, new String[]{"user", "item"});

      assertOtherEntitiesUnchanged();
  }

  @Then("deleting loan {int} should be {string} with snapshot validation")
  public void deletingLoanStatus(int id, String status) {
      try{
          loanRepository.deleteById(id);
          assertEquals("valid", status);
          assertDeletionStatusWithSnapshotValidation();
      } catch (Exception e) {
          e.printStackTrace();
          assertEquals("invalid", status);
          assertSnapshotUnchanged();
      }
  }

}