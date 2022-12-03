package com.example.testcasegenerator.controller;

import com.example.testcasegenerator.model.Title;
import com.example.testcasegenerator.repository.TitleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping(path = "/title")
@CrossOrigin(origins = "${frontend.url}")
public class TitleController {
    private final TitleRepository titleRepository;

    public TitleController(TitleRepository titleRepository) {
        this.titleRepository = titleRepository;
    }

    @GetMapping
    public Iterable<Title> getAllTitles() {
        return titleRepository.findAll();
    }

    @GetMapping(path = "/{titleId}")
    public Title getTitleById(@PathVariable int titleId) {
        return titleRepository.findById(titleId).get();
    }

    @PostMapping
    public Title addNewTitle(@RequestBody Title newTitle) {
        return titleRepository.save(newTitle);
    }

    @RequestMapping(value = "/{titleId}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<Object> updateTitle (@PathVariable int titleId, @RequestBody Title updatedTitle) {
        if(titleRepository.findById(titleId).isPresent()){
            return new ResponseEntity<>(titleRepository.save(updatedTitle), HttpStatus.OK);
        }
        else return new ResponseEntity<>(new HashMap<String, String>(1){{put("error", "No title found with ID: " + titleId);}}, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{titleId}")
    public ResponseEntity<Object> deleteTitleById(@PathVariable int titleId) {
        if(titleRepository.findById(titleId).isPresent()){
            titleRepository.deleteById(titleId);
            return new ResponseEntity<>(new HashMap<String, String>(1){{put("result", "Successfully deleted title");}}, HttpStatus.OK);
        }
        else return new ResponseEntity<>(new HashMap<String, String>(1){{put("error", "No title found with ID: " + titleId);}}, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Object> deleteAllUsers() {
        titleRepository.deleteAll();
        return new ResponseEntity<>(new HashMap<String, String>(1){{put("result", "Successfully deleted all titles");}}, HttpStatus.OK);
    }
}
