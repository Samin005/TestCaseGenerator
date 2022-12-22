package com.example.testcasegenerator.cucumber;

import org.springframework.boot.test.context.SpringBootTest;
import io.cucumber.spring.CucumberContextConfiguration;
import com.example.testcasegenerator.TestCaseGeneratorApplication;

@CucumberContextConfiguration
@SpringBootTest(classes = TestCaseGeneratorApplication.class)
public class CucumberSpringConfiguration {
}
