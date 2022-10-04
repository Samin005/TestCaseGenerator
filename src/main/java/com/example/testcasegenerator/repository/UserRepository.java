package com.example.testcasegenerator.repository;

import com.example.testcasegenerator.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

}
