package com.example.testcasegenerator.controller;

import com.example.testcasegenerator.model.Item;
import com.example.testcasegenerator.repository.ItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping(path = "/item")
@CrossOrigin(origins = "${frontend.url}")
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
    
    @RequestMapping(value = "/{itemId}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<Object> updateItem (@PathVariable int itemId, @RequestBody Item updatedItem) {
        if(itemRepository.findById(itemId).isPresent()){
            updatedItem.setId(itemId);
            return new ResponseEntity<>(itemRepository.save(updatedItem), HttpStatus.OK);
        }
        else return new ResponseEntity<>(new HashMap<String, String>(1){{put("error", "No item found with ID: " + itemId);}}, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItemById(@PathVariable int itemId) {
        if(itemRepository.findById(itemId).isPresent()){
            itemRepository.deleteById(itemId);
            return new ResponseEntity<>(new HashMap<String, String>(1){{put("result", "Successfully deleted item");}}, HttpStatus.OK);
        }
        else return new ResponseEntity<>(new HashMap<String, String>(1){{put("error", "No item found with ID: " + itemId);}}, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Object> deleteAllUsers() {
        itemRepository.deleteAll();
        return new ResponseEntity<>(new HashMap<String, String>(1){{put("result", "Successfully deleted all items");}}, HttpStatus.OK);
    }
}
