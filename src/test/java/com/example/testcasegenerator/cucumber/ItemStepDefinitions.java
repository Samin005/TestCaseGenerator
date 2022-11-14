package com.example.testcasegenerator.cucumber;

import io.cucumber.java.en.*;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;
import java.util.*;
import com.example.testcasegenerator.model.*;
import com.example.testcasegenerator.repository.*;

public class ItemStepDefinitions {
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
  private Item currentItem = new Item();

  @Given("delete existing items")
  public void deleteExistingItems() {
      itemRepository.deleteAll();
      assertEquals(0, itemRepository.count());
      System.out.println("Deleted all items");
  }

  @Given("create item with values {int} {int}")
  public void setItemProperties(int id, int title_id) {
      currentItem.setId(id);
      if(title_id != -1) 
          currentItem.setTitle(titleRepository.findById(title_id).get());
      try{
          itemRepository.save(currentItem);
          System.out.println("Saved item with id: " + currentItem.getId());
      } catch (Exception e) {
          e.printStackTrace();
      }
  }

  private Item getItemById(int id) {
      if(itemRepository.findById(id).isPresent())
          return itemRepository.findById(id).get();
      else return null;
  }

  @Given("save database snapshot for items and rest of the world")
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
      ArrayList actualEntriesList = new ArrayList((Collection) actualEntriesIterable);
      actualEntriesList.remove(actualEntriesList.size() - 1);
      Object[] actualEntriesWithoutLatest = actualEntriesList.toArray();
      Object[] expectedEntries = new ArrayList((Collection) expectedEntriesIterable).toArray();
      assertEquals(expectedEntries.length, actualEntriesWithoutLatest.length);
      for(int i = 0; i < expectedEntries.length; i++)
          assertTrue(new ReflectionEquals(expectedEntries[i], foreignKeys).matches(actualEntriesWithoutLatest[i]));
  }

  private void assertItemObjectEquals(Item item) {
      assertTrue(new ReflectionEquals(item, new String[]{"title"}).matches(currentItem));
      assertTrue(new ReflectionEquals(item.getTitle().getId()).matches(currentItem.getTitle().getId()));
  }

  private void assertOtherEntitiesUnchanged() {
      assertEntityEquals(loanSnapshot, loanRepository.findAll(), new String[]{"user", "item"});
      assertEntityEquals(titleSnapshot, titleRepository.findAll(), null);
      assertEntityEquals(userSnapshot, userRepository.findAll(), null);
  }

  private void assertCreationStatusWithSnapshotValidation(Item createdItem) {
      assertItemObjectEquals(createdItem);
      assertRemainingEntries(itemSnapshot, itemRepository.findAll(), new String[]{"title"});

      assertOtherEntitiesUnchanged();
  }

  private void assertSnapshotUnchanged() {
      assertEntityEquals(itemSnapshot, itemRepository.findAll(), new String[]{"title"});
      assertOtherEntitiesUnchanged();
  }

  @Then("create single item status should be {string} with snapshot validation")
  public void checkSingleItemCreateStatus(String status) {
      Item createdItem = getItemById(currentItem.getId());
      if(status.equals("valid")) {
          assertEquals(1, itemRepository.count());
          assertCreationStatusWithSnapshotValidation(createdItem);
      }
      else {
          assertNotEquals(1, itemRepository.count());
          assertSnapshotUnchanged();
          assertNull(createdItem);
      }
  }

  @Then("create item status should be {string} with snapshot validation")
  public void checkItemCreateStatus(String status) {
      Item createdItem = getItemById(currentItem.getId());
      if(status.equals("valid")) {
          assertCreationStatusWithSnapshotValidation(createdItem);
      }
      else assertSnapshotUnchanged();
  }

  @Then("fetching item {int} should be {string} with snapshot validation")
  public void fetchingItemStatus(int id, String status) {
      Item fetchedItem = getItemById(id);
      if(status.equals("valid")) {
          assertItemObjectEquals(fetchedItem);
      }
      else assertNull(fetchedItem);
      assertSnapshotUnchanged();
  }

  private void assertDeletionStatusWithSnapshotValidation() {
      assertRemainingEntries(itemRepository.findAll(), itemSnapshot, new String[]{"title"});

      assertOtherEntitiesUnchanged();
  }

  @Then("deleting item {int} should be {string} with snapshot validation")
  public void deletingItemStatus(int id, String status) {
      try{
          itemRepository.deleteById(id);
          assertEquals("valid", status);
          assertDeletionStatusWithSnapshotValidation();
      } catch (Exception e) {
          e.printStackTrace();
          assertEquals("invalid", status);
          assertSnapshotUnchanged();
      }
  }

}