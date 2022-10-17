package com.example.testcasegenerator.repository;

import com.example.testcasegenerator.model.Title;
import org.springframework.data.repository.CrudRepository;

public interface TitleRepository extends CrudRepository<Title, Integer> {
}
