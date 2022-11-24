Feature: Author

  Scenario Outline: create single author
      Given delete existing authors
      And save database snapshot for authors and rest of the world
      When create author with values <id> "<name>" 
      Then create single author status should be "<status>" with snapshot validation
      Examples:
         | id | name | status |
         | 1 | test1 | valid |
         | 1 | null | invalid |
         
  Scenario Outline: create multiple authors
      Given save database snapshot for authors and rest of the world
      Given create author with values <id> "<name>" 
      Then create author status should be "<status>" with snapshot validation
      Examples:
         | id | name | status |
         | 1 | test1 | valid |
         | 2 | test2 | valid |
         | 2 | null | invalid |
         
  Scenario Outline: fetch author without creation
      Given delete existing authors
      And save database snapshot for authors and rest of the world
      Then fetching author <id> should be "<status>" with snapshot validation
      Examples:
         | id | status |
         | 1 | invalid |
         | 2 | invalid |
         
  Scenario Outline: fetch author after creation
      Given delete existing authors
      And create author with values <id> "<name>" 
      And save database snapshot for authors and rest of the world
      Then fetching author <id> should be "<status>" with snapshot validation
      Examples:
         | id | name | status |
         | 1 | test1 | valid |
         | 2 | test2 | valid |
         
  Scenario Outline: delete author without creation
      Given delete existing authors
      And save database snapshot for authors and rest of the world
      Then deleting author <id> should be "<status>" with snapshot validation
      Examples:
         | id | status |
         | 1 | invalid |
         | 2 | invalid |
         
  Scenario Outline: delete author after creation
      Given delete existing authors
      And create author with values <id> "<name>" 
      And save database snapshot for authors and rest of the world
      Then deleting author <id> should be "<status>" with snapshot validation
      Examples:
         | id | name | status |
         | 1 | test1 | valid |
         | 2 | test2 | valid |
         
