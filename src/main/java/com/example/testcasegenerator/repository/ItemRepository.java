package com.example.testcasegenerator.repository;

import com.example.testcasegenerator.model.Item;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends CrudRepository<Item, Integer> {
}
