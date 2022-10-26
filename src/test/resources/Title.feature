Feature: Title

  Scenario Outline: create single title
      Given delete existing titles
      When create title with values <id> "<isbn>" 
      Then create single title status should be "<status>"
      Examples:
         | id | isbn | status |
         | 1 | test | valid |
         | 1 | null | invalid |
         
  Scenario Outline: create multiple titles
      Given create title with values <id> "<isbn>" 
      Then create title status should be "<status>"
      Examples:
         | id | isbn | status |
         | 1 | test1 | valid |
         | 2 | test2 | valid |
         | 2 | test1 | invalid |
         | 1 | null | invalid |
         
  Scenario Outline: fetch title without creation
      Given delete existing titles
      Then fetching title <id> should be "<status>"
      Examples:
         | id | status |
         | 1 | invalid |
         | 2 | invalid |
         
  Scenario Outline: fetch title after creation
      Given delete existing titles
      And create title with values <id> "<isbn>" 
      Then fetching title <id> should be "<status>"
      Examples:
         | id | isbn | status |
         | 1 | test1 | valid |
         | 2 | test2 | valid |
         
  Scenario Outline: delete title without creation
      Given delete existing titles
      Then deleting title <id> should be "<status>"
      Examples:
         | id | status |
         | 1 | invalid |
         | 2 | invalid |
         
  Scenario Outline: delete title after creation
      Given delete existing titles
      And create title with values <id> "<isbn>" 
      Then deleting title <id> should be "<status>"
      Examples:
         | id | isbn | status |
         | 1 | test1 | valid |
         | 2 | test2 | valid |
         
