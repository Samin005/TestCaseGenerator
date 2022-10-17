package com.example.testcasegenerator.controller;

import com.example.testcasegenerator.model.Item;
import com.example.testcasegenerator.repository.ItemRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/item")
public class ItemController {
    private final ItemRepository itemRepository;

    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping
    public Iterable<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @GetMapping(path = "/{itemId}")
    public Item getItemById(@PathVariable int itemId) {
        return itemRepository.findById(itemId).get();
    }

    @PostMapping
    public Item addNewItem(@RequestBody Item newItem) {
        return itemRepository.save(newItem);
    }
}
