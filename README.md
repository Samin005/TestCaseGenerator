# TestCaseGenerator
This repository aims to generate Test Cases (Cucumber Feature files) and their corresponding Step Definitions for a Java application built with Spring Boot, JPA and MySQL.

In addition, make sure to have the dependencies for JUnit, Cucumber and Cucumber-Spring. For example:

        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-java</artifactId>
            <version>7.7.0</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-junit</artifactId>
            <version>7.7.0</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>io.cucumber</groupId>
            <artifactId>cucumber-spring</artifactId>
            <version>7.7.0</version>
            <scope>test</scope>
        </dependency>