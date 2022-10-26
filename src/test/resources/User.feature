Feature: User

  Scenario Outline: create single user
      Given delete existing users
      When create user with values <id> "<name>" "<email>" 
      Then create single user status should be "<status>"
      Examples:
         | id | name | email | status |
         | 1 | test | test | valid |
         | 1 | null | test0 | invalid |
         | 1 | test1 | null | invalid |
         
  Scenario Outline: create multiple users
      Given create user with values <id> "<name>" "<email>" 
      Then create user status should be "<status>"
      Examples:
         | id | name | email | status |
         | 1 | test1 | test1 | valid |
         | 2 | test2 | test2 | valid |
         | 2 | test2 | test1 | invalid |
         | 1 | null | test0 | invalid |
         | 1 | test1 | null | invalid |
         
  Scenario Outline: fetch user without creation
      Given delete existing users
      Then fetching user <id> should be "<status>"
      Examples:
         | id | status |
         | 1 | invalid |
         | 2 | invalid |
         
  Scenario Outline: fetch user after creation
      Given delete existing users
      And create user with values <id> "<name>" "<email>" 
      Then fetching user <id> should be "<status>"
      Examples:
         | id | name | email | status |
         | 1 | test1 | test1 | valid |
         | 2 | test2 | test2 | valid |
         
  Scenario Outline: delete user without creation
      Given delete existing users
      Then deleting user <id> should be "<status>"
      Examples:
         | id | status |
         | 1 | invalid |
         | 2 | invalid |
         
  Scenario Outline: delete user after creation
      Given delete existing users
      And create user with values <id> "<name>" "<email>" 
      Then deleting user <id> should be "<status>"
      Examples:
         | id | name | email | status |
         | 1 | test1 | test1 | valid |
         | 2 | test2 | test2 | valid |
         
