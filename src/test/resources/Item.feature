Feature: Item

  Background: 
      Given create author with values 1 "test-name" 
      Given create title with values 1 "test-isbn" "test-name" 1 

  Scenario Outline: create single item
      Given delete existing items
      And save database snapshot for items and rest of the world
      When create item with values <id> <title_id> "<location>" 
      Then create single item status should be "<status>" with snapshot validation
      Examples:
         | id | title_id | location | status |
         | 1 | 1 | test1 | valid |
         | 1 | -1 | test0 | invalid |
         | 1 | 1 | null | invalid |
         
  Scenario Outline: create multiple items
      Given save database snapshot for items and rest of the world
      Given create item with values <id> <title_id> "<location>" 
      Then create item status should be "<status>" with snapshot validation
      Examples:
         | id | title_id | location | status |
         | 1 | 1 | test1 | valid |
         | 2 | 1 | test2 | valid |
         | 2 | -1 | test0 | invalid |
         | 2 | 1 | null | invalid |
         
  Scenario Outline: fetch item without creation
      Given delete existing items
      And save database snapshot for items and rest of the world
      Then fetching item <id> should be "<status>" with snapshot validation
      Examples:
         | id | status |
         | 1 | invalid |
         | 2 | invalid |
         
  Scenario Outline: fetch item after creation
      Given delete existing items
      And create item with values <id> <title_id> "<location>" 
      And save database snapshot for items and rest of the world
      Then fetching item <id> should be "<status>" with snapshot validation
      Examples:
         | id | title_id | location | status |
         | 1 | 1 | test1 | valid |
         | 2 | 1 | test2 | valid |
         
  Scenario Outline: delete item without creation
      Given delete existing items
      And save database snapshot for items and rest of the world
      Then deleting item <id> should be "<status>" with snapshot validation
      Examples:
         | id | status |
         | 1 | invalid |
         | 2 | invalid |
         
  Scenario Outline: delete item after creation
      Given delete existing items
      And create item with values <id> <title_id> "<location>" 
      And save database snapshot for items and rest of the world
      Then deleting item <id> should be "<status>" with snapshot validation
      Examples:
         | id | title_id | location | status |
         | 1 | 1 | test1 | valid |
         | 2 | 1 | test2 | valid |
         
