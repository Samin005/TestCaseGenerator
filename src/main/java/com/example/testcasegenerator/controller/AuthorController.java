package com.example.testcasegenerator.controller;

import com.example.testcasegenerator.model.Author;
import com.example.testcasegenerator.repository.AuthorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping(path = "/author")
@CrossOrigin(origins = "${frontend.url}")
public class AuthorController {
    private final AuthorRepository authorRepository;

    public AuthorController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @GetMapping
    public Iterable<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    @GetMapping(path = "/{authorId}")
    public Author getAuthorById(@PathVariable int authorId) {
        return authorRepository.findById(authorId).get();
    }

    @PostMapping
    public Author addNewAuthor(@RequestBody Author newAuthor) {
        return authorRepository.save(newAuthor);
    }

    @RequestMapping(value = "/{authorId}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<Object> updateAuthor (@PathVariable int authorId, @RequestBody Author updatedAuthor) {
        if(authorRepository.findById(authorId).isPresent()){
            updatedAuthor.setId(authorId);
            return new ResponseEntity<>(authorRepository.save(updatedAuthor), HttpStatus.OK);
        }
        else return new ResponseEntity<>(new HashMap<String, String>(1){{put("error", "No author found with ID: " + authorId);}}, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{authorId}")
    public ResponseEntity<Object> deleteAuthorById(@PathVariable int authorId) {
        if(authorRepository.findById(authorId).isPresent()){
            authorRepository.deleteById(authorId);
            return new ResponseEntity<>(new HashMap<String, String>(1){{put("result", "Successfully deleted author");}}, HttpStatus.OK);
        }
        else return new ResponseEntity<>(new HashMap<String, String>(1){{put("error", "No author found with ID: " + authorId);}}, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Object> deleteAllUsers() {
        authorRepository.deleteAll();
        return new ResponseEntity<>(new HashMap<String, String>(1){{put("result", "Successfully deleted all authors");}}, HttpStatus.OK);
    }
}
