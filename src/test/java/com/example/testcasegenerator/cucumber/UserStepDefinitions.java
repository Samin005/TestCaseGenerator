package com.example.testcasegenerator.cucumber;

import io.cucumber.java.en.*;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;
import java.util.*;
import com.example.testcasegenerator.model.*;
import com.example.testcasegenerator.repository.*;

public class UserStepDefinitions {
  @Autowired
  private AuthorRepository authorRepository;
  private Iterable<Author> authorSnapshot;
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
  private User currentUser = new User();

  @Given("delete existing users")
  public void deleteExistingUsers() {
      userRepository.deleteAll();
      assertEquals(0, userRepository.count());
      System.out.println("Deleted all users");
  }

  @Given("create user with values {int} {string} {string}")
  public void setUserProperties(int id, String name, String email) {
      currentUser.setId(id);
      name = name.equals("null") ? null : name;
      currentUser.setName(name);
      email = email.equals("null") ? null : email;
      currentUser.setEmail(email);
      try{
          userRepository.save(currentUser);
          System.out.println("Saved user with id: " + currentUser.getId());
      } catch (Exception e) {
          e.printStackTrace();
      }
  }

  private User getUserById(int id) {
      if(userRepository.findById(id).isPresent())
          return userRepository.findById(id).get();
      else return null;
  }

  @Given("save database snapshot for users and rest of the world")
  public void saveDatabaseSnapshot() {
      authorSnapshot = authorRepository.findAll();
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

  private void assertUserObjectEquals(User user) {
      assertTrue(new ReflectionEquals(user).matches(currentUser));
  }

  private void assertOtherEntitiesUnchanged() {
      assertEntityEquals(authorSnapshot, authorRepository.findAll(), null);
      assertEntityEquals(itemSnapshot, itemRepository.findAll(), new String[]{"title"});
      assertEntityEquals(loanSnapshot, loanRepository.findAll(), new String[]{"user", "item"});
      assertEntityEquals(titleSnapshot, titleRepository.findAll(), new String[]{"author"});
  }

  private void assertCreationStatusWithSnapshotValidation(User createdUser) {
      assertUserObjectEquals(createdUser);
      assertRemainingEntries(userSnapshot, userRepository.findAll(), null);

      assertOtherEntitiesUnchanged();
  }

  private void assertSnapshotUnchanged() {
      assertEntityEquals(userSnapshot, userRepository.findAll(), null);
      assertOtherEntitiesUnchanged();
  }

  @Then("create single user status should be {string} with snapshot validation")
  public void checkSingleUserCreateStatus(String status) {
      User createdUser = getUserById(currentUser.getId());
      if(status.equals("valid")) {
          assertEquals(1, userRepository.count());
          assertCreationStatusWithSnapshotValidation(createdUser);
      }
      else {
          assertNotEquals(1, userRepository.count());
          assertSnapshotUnchanged();
          assertNull(createdUser);
      }
  }

  @Then("create user status should be {string} with snapshot validation")
  public void checkUserCreateStatus(String status) {
      User createdUser = getUserById(currentUser.getId());
      if(status.equals("valid")) {
          assertCreationStatusWithSnapshotValidation(createdUser);
      }
      else assertSnapshotUnchanged();
  }

  @Then("fetching user {int} should be {string} with snapshot validation")
  public void fetchingUserStatus(int id, String status) {
      User fetchedUser = getUserById(id);
      if(status.equals("valid")) {
          assertUserObjectEquals(fetchedUser);
      }
      else assertNull(fetchedUser);
      assertSnapshotUnchanged();
  }

  private void assertDeletionStatusWithSnapshotValidation() {
      assertRemainingEntries(userRepository.findAll(), userSnapshot, null);

      assertOtherEntitiesUnchanged();
  }

  @Then("deleting user {int} should be {string} with snapshot validation")
  public void deletingUserStatus(int id, String status) {
      try{
          userRepository.deleteById(id);
          assertEquals("valid", status);
          assertDeletionStatusWithSnapshotValidation();
      } catch (Exception e) {
          e.printStackTrace();
          assertEquals("invalid", status);
          assertSnapshotUnchanged();
      }
  }

}