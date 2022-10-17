package com.example.testcasegenerator.controller;

import com.example.testcasegenerator.model.Title;
import com.example.testcasegenerator.repository.TitleRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/title")
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
}
