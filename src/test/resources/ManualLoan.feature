Feature: Loan

  Background:
    Given user properties are 1 "test-name" "test-email"
    And create current user
    And item properties are 1 1
    And create current item

  Scenario Outline: create single loan
      Given delete existing loans
      And loan properties are <id> <user_id> <item_id>
      When create current loan
      Then create single loan status should be "<status>"
      Examples:
         | id | user_id | item_id | status |
         | 1 | 1 | 1 | valid |
         | 1 | -1 | 1 | invalid |
         | 1 | 1 | -1 | invalid |
         
  Scenario Outline: create multiple loans
      Given loan properties are <id> <user_id> <item_id>
      When create current loan
      Then create loan status should be "<status>"
      Examples:
         | id | user_id | item_id | status |
         | 1 | 1 | 1 | valid |
         | 2 | 1 | 1 | valid |
         | 2 | -1 | 1 | invalid |
         | 2 | 1 | -1 | invalid |
         
  Scenario Outline: fetch loan without creation
      Given delete existing loans
      Then fetching loan <id> should be "<status>"
      Examples:
         | id | status |
         | 1 | invalid |
         | 2 | invalid |
         
  Scenario Outline: fetch loan after creation
      Given delete existing loans
      And loan properties are <id> <user_id> <item_id>
      And create current loan
      Then fetching loan <id> should be "<status>"
      Examples:
         | id | user_id | item_id | status |
         | 1 | 1 | 1 | valid |
         | 2 | 1 | 1 | valid |
         
  Scenario Outline: delete loan without creation
      Given delete existing loans
      Then deleting loan <id> should be "<status>"
      Examples:
         | id | status |
         | 1 | invalid |
         | 2 | invalid |
         
  Scenario Outline: delete loan after creation
      Given delete existing loans
      And loan properties are <id> <user_id> <item_id>
      And create current loan
      Then deleting loan <id> should be "<status>"
      Examples:
         | id | user_id | item_id | status |
         | 1 | 1 | 1 | valid |
         | 2 | 1 | 1 | valid |
         
