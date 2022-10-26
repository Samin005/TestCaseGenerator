package com.example.testcasegenerator.cucumber;

import io.cucumber.java.en.*;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;
import com.example.testcasegenerator.model.Title;
import com.example.testcasegenerator.repository.TitleRepository;

public class TitleStepDefinitions {
  @Autowired
  private TitleRepository titleRepository;
  private Title currentTitle = new Title();

  @Given("delete existing titles")
  public void deleteExistingTitles() {
      titleRepository.deleteAll();
      assertEquals(0, titleRepository.count());
      System.out.println("Deleted all titles");
  }

  @Given("create title with values {int} {string}")
  public void setTitleProperties(int id, String isbn) {
      currentTitle.setId(id);
      isbn = isbn.equals("null") ? null : isbn;
      currentTitle.setIsbn(isbn);
      try{
          titleRepository.save(currentTitle);
          System.out.println("Saved title with id: " + currentTitle.getId());
      } catch (Exception e) {
          e.printStackTrace();
      }
  }

  private Title getTitleById(int id) {
      if(titleRepository.findById(id).isPresent())
          return titleRepository.findById(id).get();
      else return null;
  }

  @Then("create single title status should be {string}")
  public void checkSingleTitleCreateStatus(String status) {
      Title createdTitle = getTitleById(currentTitle.getId());
      if(status.equals("valid")) {
          assertEquals(1, titleRepository.count());
          assertTrue(new ReflectionEquals(createdTitle).matches(currentTitle));
      }
      else {
          assertNotEquals(1, titleRepository.count());
          assertFalse(new ReflectionEquals(createdTitle).matches(currentTitle));
      }
  }

  @Then("create title status should be {string}")
  public void checkTitleCreateStatus(String status) {
      Title createdTitle = getTitleById(currentTitle.getId());
      if(status.equals("valid")) {
          assertTrue(new ReflectionEquals(createdTitle).matches(currentTitle));
      }
      else assertFalse(new ReflectionEquals(createdTitle).matches(currentTitle));
  }

  @Then("fetching title {int} should be {string}")
  public void fetchingTitleStatus(int id, String status) {
      Title fetchedTitle = getTitleById(id);
      if(status.equals("valid")) {
          assertTrue(new ReflectionEquals(fetchedTitle).matches(currentTitle));
      }
      else assertNull(fetchedTitle);
  }

  @Then("deleting title {int} should be {string}")
  public void deletingTitleStatus(int id, String status) {
      try{
          titleRepository.deleteById(id);
          assertEquals("valid", status);
      } catch (Exception e) {
          e.printStackTrace();
          assertEquals("invalid", status);
      }
  }

}