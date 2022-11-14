//package com.example.testcasegenerator.cucumber;
//
//import com.example.testcasegenerator.model.*;
//import com.example.testcasegenerator.repository.*;
//import io.cucumber.java.en.Given;
//import io.cucumber.java.en.Then;
//import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.*;
//
//import static org.junit.Assert.*;
//
//public class ManualTitleStepDefinitions {
//    @Autowired
//    public UserRepository userRepository;
//    @Autowired
//    public TitleRepository titleRepository;
//    @Autowired
//    public ItemRepository itemRepository;
//    @Autowired
//    public LoanRepository loanRepository;
//    public Iterable<User> userSnapshot;
//    public Iterable<Title> titleSnapshot;
//    public Iterable<Item> itemSnapshot;
//    public Iterable<Loan> loanSnapshot;
//  private Title currentTitle = new Title();
//
//  @Given("delete existing titles")
//  public void deleteExistingTitles() {
//      titleRepository.deleteAll();
//      assertEquals(0, titleRepository.count());
//      System.out.println("Deleted all titles");
//  }
//
//  @Given("create title with values {int} {string}")
//  public void setTitleProperties(int id, String isbn) {
//      currentTitle.setId(id);
//      isbn = isbn.equals("null") ? null : isbn;
//      currentTitle.setIsbn(isbn);
//      try{
//          titleRepository.save(currentTitle);
//          System.out.println("Saved title with id: " + currentTitle.getId());
//      } catch (Exception e) {
//          e.printStackTrace();
//      }
//  }
//
//  private Title getTitleById(int id) {
//      if(titleRepository.findById(id).isPresent())
//          return titleRepository.findById(id).get();
//      else return null;
//  }
//
//    @Given("save database snapshot for titles and rest of the world")
//    public void saveDatabaseSnapshot() {
//        userSnapshot = userRepository.findAll();
//        titleSnapshot = titleRepository.findAll();
//        itemSnapshot = itemRepository.findAll();
//        loanSnapshot = loanRepository.findAll();
//    }
//
//    private void assertEntityEquals(Iterable expectedEntityIterable, Iterable actualEntityIterable, String[] foreignKeys) {
//        Object[] expectedEntity = new ArrayList((Collection) expectedEntityIterable).toArray();
//        Object[] actualEntity = new ArrayList((Collection) actualEntityIterable).toArray();
//        assertEquals(expectedEntity.length, actualEntity.length);
//        for (int i = 0; i < expectedEntity.length; i++) {
//            assertTrue(new ReflectionEquals(expectedEntity[i], foreignKeys).matches(actualEntity[i]));
//        }
//    }
//
//    private void assertRemainingEntries(Iterable expectedEntriesIterable, Iterable actualEntriesIterable, String[] foreignKeys) {
//        ArrayList actualEntriesList = new ArrayList((Collection) actualEntriesIterable);
//        actualEntriesList.remove(actualEntriesList.size() - 1);
//        Object[] actualEntriesWithoutLatest = actualEntriesList.toArray();
//        Object[] expectedEntries = new ArrayList((Collection) expectedEntriesIterable).toArray();
//        assertEquals(expectedEntries.length, actualEntriesWithoutLatest.length);
//        for (int i = 0; i < expectedEntries.length; i++) {
//            assertTrue(new ReflectionEquals(expectedEntries[i], foreignKeys).matches(actualEntriesWithoutLatest[i]));
//        }
//    }
//
//    private void assertTitleObjectEquals(Title title) {
//        assertTrue(new ReflectionEquals(title).matches(currentTitle));
//    }
//
//    private void assertOtherEntitiesUnchanged() {
//        assertEntityEquals(userSnapshot, userRepository.findAll(), null);
//        assertEntityEquals(itemSnapshot, itemRepository.findAll(), new String[]{"title"});
//        assertEntityEquals(loanSnapshot, loanRepository.findAll(), new String[]{"item", "user"});
//    }
//
//    private void assertCreationStatusWithSnapshotValidation(Title createdTitle) {
//        assertTitleObjectEquals(createdTitle);
//        assertRemainingEntries(titleSnapshot, titleRepository.findAll(), null);
//
//        assertOtherEntitiesUnchanged();
//    }
//
//    private void assertSnapshotUnchanged() {
//        assertEntityEquals(titleSnapshot, titleRepository.findAll(), null);
//        assertOtherEntitiesUnchanged();
//    }
//
//  @Then("create single title status should be {string} with snapshot validation")
//  public void checkSingleTitleCreateStatus(String status) {
//      Title createdTitle = getTitleById(currentTitle.getId());
//      if(status.equals("valid")) {
//          assertEquals(1, titleRepository.count());
//          assertCreationStatusWithSnapshotValidation(createdTitle);
//      }
//      else {
//          assertNotEquals(1, titleRepository.count());
//          assertSnapshotUnchanged();
//          assertNull(createdTitle);
//      }
//  }
//
//  @Then("create title status should be {string} with snapshot validation")
//  public void checkTitleCreateStatus(String status) {
//      Title createdTitle = getTitleById(currentTitle.getId());
//      if(status.equals("valid")) {
//          assertCreationStatusWithSnapshotValidation(createdTitle);
//      }
//      else assertSnapshotUnchanged();
//  }
//
//  @Then("fetching title {int} should be {string} with snapshot validation")
//  public void fetchingTitleStatus(int id, String status) {
//      Title fetchedTitle = getTitleById(id);
//      if(status.equals("valid")) {
//          assertTitleObjectEquals(fetchedTitle);
//      }
//      else assertNull(fetchedTitle);
//      assertSnapshotUnchanged();
//  }
//
//    private void assertDeletionStatusWithSnapshotValidation() {
//        assertRemainingEntries(titleRepository.findAll(), titleSnapshot, new String[]{"item", "user"});
//
//        assertOtherEntitiesUnchanged();
//    }
//
//  @Then("deleting title {int} should be {string} with snapshot validation")
//  public void deletingTitleStatus(int id, String status) {
//      try{
//          titleRepository.deleteById(id);
//          assertEquals("valid", status);
//          assertDeletionStatusWithSnapshotValidation();
//      } catch (Exception e) {
//          e.printStackTrace();
//          assertEquals("invalid", status);
//          assertSnapshotUnchanged();
//      }
//  }
//
//}