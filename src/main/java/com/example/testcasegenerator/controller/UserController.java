package com.example.testcasegenerator.controller;

import com.example.testcasegenerator.model.User;
import com.example.testcasegenerator.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/user")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable int userId) {
        if(userRepository.findById(userId).isPresent())
            return new ResponseEntity<>(userRepository.findById(userId).get(), HttpStatus.OK);
        else return new ResponseEntity<>("No user found with ID: " + userId, HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public User addNewUser (@RequestBody User newUser) {
        return userRepository.save(newUser);
    }

    @RequestMapping(value = "/{userId}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<Object> updateUser (@PathVariable int userId, @RequestBody User updatedUser) {
        if(userRepository.findById(userId).isPresent()){
            User userToUpdate = userRepository.findById(userId).get();
            if(updatedUser.getName()!=null) userToUpdate.setName(updatedUser.getName());
            if(updatedUser.getEmail()!=null) userToUpdate.setEmail(updatedUser.getEmail());
            return new ResponseEntity<>(userRepository.save(userToUpdate), HttpStatus.OK);
        }
        else return new ResponseEntity<>("No user found with ID: " + userId, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable int userId) {
        if(userRepository.findById(userId).isPresent()){
            userRepository.deleteById(userId);
            return new ResponseEntity<>("Successfully deleted user", HttpStatus.OK);
        }
        else return new ResponseEntity<>("No user found with ID: " + userId, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Object> deleteAllUsers() {
        userRepository.deleteAll();
        return new ResponseEntity<>("Successfully deleted all users", HttpStatus.OK);
    }

}
