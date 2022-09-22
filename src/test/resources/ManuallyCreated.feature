Feature: Create User

  Scenario Outline: create single user
    Given delete existing users
    And user properties are <id> "<name>" "<email>"
    When create current user
    Then create single user status should be "<status>"
    Examples:
      | id | name | email          | status  |
      | 1  | Fred | fred@gmail.com | valid   |
      | 1  | Bob  | bob@gmail.com  | valid   |
      | 1  | null | abc@gmail.com  | invalid |
      | 1  | Bill | null           | invalid |


  Scenario Outline: create multiple users
    Given user properties are <id> "<name>" "<email>"
    When create current user
    Then create user status should be "<status>"
    Examples:
      | id | name | email          | status  |
      | 1  | Fred | fred@gmail.com | valid   |
      | 2  | Bob  | bob@gmail.com  | valid   |
      | 3  | Bob  | bob@gmail.com  | invalid |
      | 3  | null | abc@gmail.com  | invalid |
      | 3  | Bill | null           | invalid |
      | 1  | Fred | null           | invalid |


  Scenario Outline: fetch user without creation
    Given delete existing users
    When fetch user with id <id>
    Then fetching <id> should be "<status>"
    Examples:
      | id | status  |
      | 1  | invalid |
      | 3  | invalid |


  Scenario Outline: fetch user after creation
    Given delete existing users
    And user properties are <id> "<name>" "<email>"
    And create current user
    When fetch user with id <id>
    Then fetching <id> should be "<status>"
    Examples:
      | id | name | email          | status  |
      | 1  | Fred | fred@gmail.com | valid   |
      | 2  | Bob  | bob@gmail.com  | valid   |


  Scenario Outline: delete user without creation
    Given delete existing users
    Then deleting <id> should be "<status>"
    Examples:
      | id | status  |
      | 1  | invalid |
      | 3  | invalid |


  Scenario Outline: delete user after creation
    Given delete existing users
    And user properties are <id> "<name>" "<email>"
    And create current user
    Then deleting <id> should be "<status>"
    Examples:
      | id | name | email          | status  |
      | 1  | Fred | fred@gmail.com | valid   |
      | 2  | Bob  | bob@gmail.com  | valid   |
