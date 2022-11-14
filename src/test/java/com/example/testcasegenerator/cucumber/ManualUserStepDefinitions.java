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
//public class ManualUserStepDefinitions {
//    @Autowired
//    public UserRepository userRepository;
//    @Autowired
//    public TitleRepository titleRepository;
//    @Autowired
//    public ItemRepository itemRepository;
//    @Autowired
//    public LoanRepository loanRepository;
//    private User currentUser = new User();
//    public Iterable<User> userSnapshot;
//    public Iterable<Title> titleSnapshot;
//    public Iterable<Item> itemSnapshot;
//    public Iterable<Loan> loanSnapshot;
//
//    @Given("delete existing users")
//    public void deleteExistingUsers() {
//        userRepository.deleteAll();
//        assertEquals(0, userRepository.count());
//        System.out.println("Deleted all users");
//    }
//
//    @Given("create user with values {int} {string} {string}")
//    public void setUserProperties(int id, String name, String email) {
//        currentUser.setId(id);
//        name = name.equals("null") ? null : name;
//        currentUser.setName(name);
//        email = email.equals("null") ? null : email;
//        currentUser.setEmail(email);
//        try {
//            userRepository.save(currentUser);
//            System.out.println("Saved user with id: " + currentUser.getId());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private User getUserById(int id) {
//        if (userRepository.findById(id).isPresent())
//            return userRepository.findById(id).get();
//        else return null;
//    }
//
//    @Given("save database snapshot for users and rest of the world")
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
//    private void assertUserObjectEquals(User user) {
//        assertTrue(new ReflectionEquals(user).matches(currentUser));
//    }
//
//    private void assertOtherEntitiesUnchanged() {
//        assertEntityEquals(titleSnapshot, titleRepository.findAll(), null);
//        assertEntityEquals(itemSnapshot, itemRepository.findAll(), new String[]{"title"});
//        assertEntityEquals(loanSnapshot, loanRepository.findAll(), new String[]{"item", "user"});
//    }
//
//    private void assertCreationStatusWithSnapshotValidation(User createdUser) {
//        assertUserObjectEquals(createdUser);
//        assertRemainingEntries(userSnapshot, userRepository.findAll(), null);
//
//        assertOtherEntitiesUnchanged();
//    }
//
//    private void assertSnapshotUnchanged() {
//        assertEntityEquals(userSnapshot, userRepository.findAll(), null);
//        assertOtherEntitiesUnchanged();
//    }
//
//    @Then("create single user status should be {string} with snapshot validation")
//    public void checkSingleUserCreateStatus(String status) {
//        User createdUser = getUserById(currentUser.getId());
//        if (status.equals("valid")) {
//            assertEquals(1, userRepository.count());
//            assertCreationStatusWithSnapshotValidation(createdUser);
//        } else {
//            assertNotEquals(1, userRepository.count());
//            assertSnapshotUnchanged();
//            assertNull(createdUser);
//        }
//    }
//
//    @Then("create user status should be {string} with snapshot validation")
//    public void checkUserCreateStatus(String status) {
//        User createdUser = getUserById(currentUser.getId());
//        if (status.equals("valid")) {
//            assertCreationStatusWithSnapshotValidation(createdUser);
//        } else assertSnapshotUnchanged();
//    }
//
//    @Then("fetching user {int} should be {string} with snapshot validation")
//    public void fetchingUserStatus(int id, String status) {
//        User fetchedUser = getUserById(id);
//        if (status.equals("valid")) {
//            assertUserObjectEquals(fetchedUser);
//        } else assertNull(fetchedUser);
//        assertSnapshotUnchanged();
//    }
//
//    private void assertDeletionStatusWithSnapshotValidation() {
//        assertRemainingEntries(userRepository.findAll(), userSnapshot, new String[]{"item", "user"});
//
//        assertOtherEntitiesUnchanged();
//    }
//
//    @Then("deleting user {int} should be {string} with snapshot validation")
//    public void deletingUserStatus(int id, String status) {
//        try {
//            userRepository.deleteById(id);
//            assertEquals("valid", status);
//            assertDeletionStatusWithSnapshotValidation();
//        } catch (Exception e) {
//            e.printStackTrace();
//            assertEquals("invalid", status);
//            assertSnapshotUnchanged();
//        }
//    }
//
//}