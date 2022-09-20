package com.example.testcasegenerator.cucumber;
import com.example.testcasegenerator.TestCaseGeneratorApplication;
import org.springframework.boot.test.context.SpringBootTest;

import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(classes = TestCaseGeneratorApplication.class)
public class CucumberSpringConfiguration {
}
