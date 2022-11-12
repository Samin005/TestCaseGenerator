Feature: Title

  Scenario Outline: create single title
      Given delete existing titles
      And save database snapshot for titles and rest of the world
      When create title with values <id> "<isbn>" 
      Then create single title status should be "<status>" with snapshot validation
      Examples:
         | id | isbn | status |
         | 1 | test | valid |
         | 1 | null | invalid |
         
  Scenario Outline: create multiple titles
      Given save database snapshot for titles and rest of the world
      Given create title with values <id> "<isbn>" 
      Then create title status should be "<status>" with snapshot validation
      Examples:
         | id | isbn | status |
         | 1 | test1 | valid |
         | 2 | test2 | valid |
         | 2 | test1 | invalid |
         | 1 | null | invalid |
         
  Scenario Outline: fetch title without creation
      Given delete existing titles
      And save database snapshot for titles and rest of the world
      Then fetching title <id> should be "<status>" with snapshot validation
      Examples:
         | id | status |
         | 1 | invalid |
         | 2 | invalid |
         
  Scenario Outline: fetch title after creation
      Given delete existing titles
      And create title with values <id> "<isbn>" 
      And save database snapshot for titles and rest of the world
      Then fetching title <id> should be "<status>" with snapshot validation
      Examples:
         | id | isbn | status |
         | 1 | test1 | valid |
         | 2 | test2 | valid |
         
  Scenario Outline: delete title without creation
      Given delete existing titles
      And save database snapshot for titles and rest of the world
      Then deleting title <id> should be "<status>" with snapshot validation
      Examples:
         | id | status |
         | 1 | invalid |
         | 2 | invalid |
         
  Scenario Outline: delete title after creation
      Given delete existing titles
      And create title with values <id> "<isbn>" 
      And save database snapshot for titles and rest of the world
      Then deleting title <id> should be "<status>" with snapshot validation
      Examples:
         | id | isbn | status |
         | 1 | test1 | valid |
         | 2 | test2 | valid |
         
