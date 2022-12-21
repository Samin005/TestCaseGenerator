Feature: Loan

  Background: 
      Given create user with values 1 "test-name" "test-email" 
      Given create author with values 1 "test-name" 
      Given create title with values 1 "test-isbn" "test-name" 1 
      Given create item with values 1 1 "test-location" 

  Scenario Outline: create single loan
      Given delete existing loans
      And save database snapshot for loans and rest of the world
      When create loan with values <id> <user_id> <item_id> <renewal> <created_due_interval> 
      Then create single loan status should be "<status>" with snapshot validation
      Examples:
         | id | user_id | item_id | renewal | created_due_interval | status |
         | 1 | 1 | 1 | 2 | 20 | valid |
         | 1 | -1 | 1 | 1 | 20 | invalid |
         | 1 | 1 | -1 | 1 | 20 | invalid |
         | 1 | 1 | 1 | 1 | 20 | valid |
         | 1 | 1 | 1 | 3 | 20 | invalid |
         | 1 | 1 | 1 | 2 | 19 | valid |
         | 1 | 1 | 1 | 2 | 21 | invalid |
         
  Scenario Outline: create multiple loans
      Given save database snapshot for loans and rest of the world
      Given create loan with values <id> <user_id> <item_id> <renewal> <created_due_interval> 
      Then create loan status should be "<status>" with snapshot validation
      Examples:
         | id | user_id | item_id | renewal | created_due_interval | status |
         | 1 | 1 | 1 | 2 | 20 | valid |
         | 2 | 1 | 1 | 2 | 20 | valid |
         | 2 | -1 | 1 | 1 | 20 | invalid |
         | 2 | 1 | -1 | 1 | 20 | invalid |
         | 2 | 1 | 1 | 1 | 20 | valid |
         | 2 | 1 | 1 | 3 | 20 | invalid |
         | 2 | 1 | 1 | 2 | 19 | valid |
         | 2 | 1 | 1 | 2 | 21 | invalid |
         
  Scenario Outline: fetch loan without creation
      Given delete existing loans
      And save database snapshot for loans and rest of the world
      Then fetching loan <id> should be "<status>" with snapshot validation
      Examples:
         | id | status |
         | 1 | invalid |
         | 2 | invalid |
         
  Scenario Outline: fetch loan after creation
      Given delete existing loans
      And create loan with values <id> <user_id> <item_id> <renewal> <created_due_interval> 
      And save database snapshot for loans and rest of the world
      Then fetching loan <id> should be "<status>" with snapshot validation
      Examples:
         | id | user_id | item_id | renewal | created_due_interval | status |
         | 1 | 1 | 1 | 2 | 20 | valid |
         | 2 | 1 | 1 | 2 | 20 | valid |
         
  Scenario Outline: delete loan without creation
      Given delete existing loans
      And save database snapshot for loans and rest of the world
      Then deleting loan <id> should be "<status>" with snapshot validation
      Examples:
         | id | status |
         | 1 | invalid |
         | 2 | invalid |
         
  Scenario Outline: delete loan after creation
      Given delete existing loans
      And create loan with values <id> <user_id> <item_id> <renewal> <created_due_interval> 
      And save database snapshot for loans and rest of the world
      Then deleting loan <id> should be "<status>" with snapshot validation
      Examples:
         | id | user_id | item_id | renewal | created_due_interval | status |
         | 1 | 1 | 1 | 2 | 20 | valid |
         | 2 | 1 | 1 | 2 | 20 | valid |
         
