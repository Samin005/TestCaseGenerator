package com.example.testcasegenerator.cucumber;

import io.cucumber.java.en.*;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;
import com.example.testcasegenerator.model.*;
import com.example.testcasegenerator.repository.ItemRepository;

public class ManualItemStepDefinitions {
  @Autowired
  private ItemRepository itemRepository;

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
      if(title_id != -1) {
          Title title = new Title();
          title.setId(title_id);
          currentItem.setTitle(title);
      }
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

  @Then("create single item status should be {string}")
  public void checkSingleItemCreateStatus(String status) {
      Item createdItem = getItemById(currentItem.getId());
      if(status.equals("valid")) {
          assertEquals(1, itemRepository.count());
          assertTrue(new ReflectionEquals(createdItem, new String[] {"title"}).matches(currentItem));
          assertTrue(new ReflectionEquals(createdItem.getTitle().getId()).matches(currentItem.getTitle().getId()));
      }
      else {
          assertNotEquals(1, itemRepository.count());
          assertNull(createdItem);
      }
  }

  @Then("create item status should be {string}")
  public void checkItemCreateStatus(String status) {
      Item createdItem = getItemById(currentItem.getId());
      if(status.equals("valid")) {
          assertTrue(new ReflectionEquals(createdItem, "title").matches(currentItem));
          assertTrue(new ReflectionEquals(createdItem.getTitle().getId()).matches(currentItem.getTitle().getId()));
      }
      else assertFalse(new ReflectionEquals(createdItem).matches(currentItem));
  }

  @Then("fetching item {int} should be {string}")
  public void fetchingItemStatus(int id, String status) {
      Item fetchedItem = getItemById(id);
      if(status.equals("valid")) {
          assertTrue(new ReflectionEquals(fetchedItem, "title").matches(currentItem));
          assertTrue(new ReflectionEquals(fetchedItem.getTitle().getId()).matches(currentItem.getTitle().getId()));
      }
      else assertNull(fetchedItem);
  }

  @Then("deleting item {int} should be {string}")
  public void deletingItemStatus(int id, String status) {
      try{
          itemRepository.deleteById(id);
          assertEquals("valid", status);
      } catch (Exception e) {
          e.printStackTrace();
          assertEquals("invalid", status);
      }
  }

}