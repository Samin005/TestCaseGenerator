package com.example.testcasegenerator.cucumber;

import io.cucumber.java.en.*;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;
import java.util.*;
import com.example.testcasegenerator.model.*;
import com.example.testcasegenerator.repository.*;

public class AuthorStepDefinitions {
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
  private Author currentAuthor = new Author();

  @Given("delete existing authors")
  public void deleteExistingAuthors() {
      authorRepository.deleteAll();
      assertEquals(0, authorRepository.count());
      System.out.println("Deleted all authors");
  }

  @Given("create author with values {int} {string}")
  public void setAuthorProperties(int id, String name) {
      currentAuthor.setId(id);
      name = name.equals("null") ? null : name;
      currentAuthor.setName(name);
      try{
          authorRepository.save(currentAuthor);
          System.out.println("Saved author with id: " + currentAuthor.getId());
      } catch (Exception e) {
          e.printStackTrace();
      }
  }

  private Author getAuthorById(int id) {
      if(authorRepository.findById(id).isPresent())
          return authorRepository.findById(id).get();
      else return null;
  }

  @Given("save database snapshot for authors and rest of the world")
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

  private void assertAuthorObjectEquals(Author author) {
      assertTrue(new ReflectionEquals(author).matches(currentAuthor));
  }

  private void assertOtherEntitiesUnchanged() {
      assertEntityEquals(itemSnapshot, itemRepository.findAll(), new String[]{"title"});
      assertEntityEquals(loanSnapshot, loanRepository.findAll(), new String[]{"user", "item"});
      assertEntityEquals(titleSnapshot, titleRepository.findAll(), new String[]{"author"});
      assertEntityEquals(userSnapshot, userRepository.findAll(), null);
  }

  private void assertCreationStatusWithSnapshotValidation(Author createdAuthor) {
      assertAuthorObjectEquals(createdAuthor);
      assertRemainingEntries(authorSnapshot, authorRepository.findAll(), null);

      assertOtherEntitiesUnchanged();
  }

  private void assertSnapshotUnchanged() {
      assertEntityEquals(authorSnapshot, authorRepository.findAll(), null);
      assertOtherEntitiesUnchanged();
  }

  @Then("create single author status should be {string} with snapshot validation")
  public void checkSingleAuthorCreateStatus(String status) {
      Author createdAuthor = getAuthorById(currentAuthor.getId());
      if(status.equals("valid")) {
          assertEquals(1, authorRepository.count());
          assertCreationStatusWithSnapshotValidation(createdAuthor);
      }
      else {
          assertNotEquals(1, authorRepository.count());
          assertSnapshotUnchanged();
          assertNull(createdAuthor);
      }
  }

  @Then("create author status should be {string} with snapshot validation")
  public void checkAuthorCreateStatus(String status) {
      Author createdAuthor = getAuthorById(currentAuthor.getId());
      if(status.equals("valid")) {
          assertCreationStatusWithSnapshotValidation(createdAuthor);
      }
      else assertSnapshotUnchanged();
  }

  @Then("fetching author {int} should be {string} with snapshot validation")
  public void fetchingAuthorStatus(int id, String status) {
      Author fetchedAuthor = getAuthorById(id);
      if(status.equals("valid")) {
          assertAuthorObjectEquals(fetchedAuthor);
      }
      else assertNull(fetchedAuthor);
      assertSnapshotUnchanged();
  }

  private void assertDeletionStatusWithSnapshotValidation() {
      assertRemainingEntries(authorRepository.findAll(), authorSnapshot, null);

      assertOtherEntitiesUnchanged();
  }

  @Then("deleting author {int} should be {string} with snapshot validation")
  public void deletingAuthorStatus(int id, String status) {
      try{
          authorRepository.deleteById(id);
          assertEquals("valid", status);
          assertDeletionStatusWithSnapshotValidation();
      } catch (Exception e) {
          e.printStackTrace();
          assertEquals("invalid", status);
          assertSnapshotUnchanged();
      }
  }

}