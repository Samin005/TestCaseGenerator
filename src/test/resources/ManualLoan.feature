#Feature: Loan
#
#  Background:
#    Given create user with values 1 "test-name" "test-email"
#    And create item with values 1 1
#
#  Scenario Outline: create single loan
#    Given delete existing loans
#    And save database snapshot for loans and rest of the world
#    And create loan with values <id> <user_id> <item_id>
#    Then create single loan status should be "<status>" with snapshot validation
#    Examples:
#      | id | user_id | item_id | status  |
#      | 1  | 1       | 1       | valid   |
#      | 1  | -1      | 1       | invalid |
#      | 1  | 1       | -1      | invalid |
#
#  Scenario Outline: create multiple loans
#    Given save database snapshot for loans and rest of the world
#    And create loan with values <id> <user_id> <item_id>
#    Then create loan status should be "<status>" with snapshot validation
#    Examples:
#      | id | user_id | item_id | status  |
#      | 1  | 1       | 1       | valid   |
#      | 2  | 1       | 1       | valid   |
#      | 2  | -1      | 1       | invalid |
#      | 2  | 1       | -1      | invalid |
#
#  Scenario Outline: fetch loan without creation
#    Given delete existing loans
#    And save database snapshot for loans and rest of the world
#    Then fetching loan <id> should be "<status>" with snapshot validation
#    Examples:
#      | id | status  |
#      | 1  | invalid |
#      | 2  | invalid |
#
#  Scenario Outline: fetch loan after creation
#    Given delete existing loans
#    And create loan with values <id> <user_id> <item_id>
#    And save database snapshot for loans and rest of the world
#    Then fetching loan <id> should be "<status>" with snapshot validation
#    Examples:
#      | id | user_id | item_id | status |
#      | 1  | 1       | 1       | valid  |
#      | 2  | 1       | 1       | valid  |
#
#  Scenario Outline: delete loan without creation
#    Given delete existing loans
#    And save database snapshot for loans and rest of the world
#    Then deleting loan <id> should be "<status>" with snapshot validation
#    Examples:
#      | id | status  |
#      | 1  | invalid |
#      | 2  | invalid |
#
#  Scenario Outline: delete loan after creation
#    Given delete existing loans
#    And create loan with values <id> <user_id> <item_id>
#    And save database snapshot for loans and rest of the world
#    Then deleting loan <id> should be "<status>" with snapshot validation
#    Examples:
#      | id | user_id | item_id | status |
#      | 1  | 1       | 1       | valid  |
#      | 2  | 1       | 1       | valid  |
#
