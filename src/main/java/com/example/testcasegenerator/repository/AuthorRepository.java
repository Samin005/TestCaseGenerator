package com.example.testcasegenerator.repository;

import com.example.testcasegenerator.model.Author;
import org.springframework.data.repository.CrudRepository;

public interface AuthorRepository extends CrudRepository<Author, Integer> {
}
