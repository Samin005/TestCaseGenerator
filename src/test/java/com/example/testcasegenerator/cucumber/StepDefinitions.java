//package com.example.testcasegenerator.cucumber;
//
//import io.cucumber.java.en.*;
//import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
//import org.springframework.beans.factory.annotation.Autowired;
//import static org.junit.Assert.*;
//import com.example.testcasegenerator.model.*;
//import com.example.testcasegenerator.repository.*;
//
//public class StepDefinitions{
//    @Autowired
//    public UserRepository userRepository;
//    @Autowired
//    public TitleRepository titleRepository;
//    @Autowired
//    public ItemRepository itemRepository;
//    @Autowired
//    public LoanRepository loanRepository;
//
//    public User currentUser = new User();
//    public Title currentTitle = new Title();
//    public Item currentItem = new Item();
//    public Loan currentLoan = new Loan();
//
//    public Iterable<User> userSnapshot;
//    public Iterable<Title> titleSnapshot;
//    public Iterable<Item> itemSnapshot;
//    public Iterable<Loan> loanSnapshot;
//
//    @Given("save database snapshot")
//    public void saveDatabaseSnapshot() {
//        userSnapshot = userRepository.findAll();
//        titleSnapshot = titleRepository.findAll();
//        itemSnapshot = itemRepository.findAll();
//        loanSnapshot = loanRepository.findAll();
//    }
//
//    @Given("delete existing users")
//    public void deleteExistingUsers() {
//        userRepository.deleteAll();
//        assertEquals(0, userRepository.count());
//        System.out.println("Deleted all users");
//    }
//
//    @Given("user properties are {int} {string} {string}")
//    public void setUserProperties(int id, String name, String email) {
//        currentUser.setId(id);
//        name = name.equals("null") ? null : name;
//        currentUser.setName(name);
//        email = email.equals("null") ? null : email;
//        currentUser.setEmail(email);
//    }
//
//    @When("create current user")
//    public void createCurrentUser() {
//        try{
//            userRepository.save(currentUser);
//            System.out.println("Saved user with id: " + currentUser.getId());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Then("create single user status should be {string}")
//    public void checkSingleUserCreateStatus(String status) {
//        User createdUser = getUserById(currentUser.getId());
//        if(status.equals("valid")) {
//            assertEquals(1, userRepository.count());
//            assertTrue(new ReflectionEquals(createdUser).matches(currentUser));
//        }
//        else {
//            assertNotEquals(1, userRepository.count());
//            assertFalse(new ReflectionEquals(createdUser).matches(currentUser));
//        }
//    }
//
//    @Then("create user status should be {string}")
//    public void checkUserCreateStatus(String status) {
//        User createdUser = getUserById(currentUser.getId());
//        if(status.equals("valid")) {
//            assertTrue(new ReflectionEquals(createdUser).matches(currentUser));
//        }
//        else assertFalse(new ReflectionEquals(createdUser).matches(currentUser));
//    }
//
////    @When("fetch user with id {int}")
////    public void fetchUserWithId(int id) {
////        currentUser = getUserById(id);
////    }
//
//    @Then("fetching {int} should be {string}")
//    public void fetchingUserStatus(int id, String status) {
//        User fetchedUser = getUserById(id);
//        if(status.equals("valid")) {
//            assertTrue(new ReflectionEquals(fetchedUser).matches(currentUser));
//        }
//        else assertNull(fetchedUser);
//    }
//
//    @Then("deleting {int} should be {string}")
//    public void deletingUserStatus(int id, String status) {
//        try{
//            userRepository.deleteById(id);
//            assertEquals("valid", status);
//        } catch (Exception e) {
//            e.printStackTrace();
//            assertEquals("invalid", status);
//        }
//    }
//
//    private User getUserById(int id) {
//        if(userRepository.findById(id).isPresent())
//            return userRepository.findById(id).get();
//        else return null;
//    }

//}
