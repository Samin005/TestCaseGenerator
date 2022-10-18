Feature: Item

  Background:
    Given title properties are 1 "test-isbn"
    And create current title

  Scenario Outline: create single item
    Given delete existing items
    And item properties are <id> <title_id>
    When create current item
    Then create single item status should be "<status>"
    Examples:
      | id | title_id | status  |
      | 1  | 1        | valid   |
      | 1  | -1       | invalid |

  Scenario Outline: create multiple items
    Given item properties are <id> <title_id>
    When create current item
    Then create item status should be "<status>"
    Examples:
      | id | title_id | status  |
      | 1  | 1        | valid   |
      | 2  | 1        | valid   |
      | 2  | -1       | invalid |

  Scenario Outline: fetch item without creation
    Given delete existing items
    Then fetching item <id> should be "<status>"
    Examples:
      | id | status  |
      | 1  | invalid |
      | 2  | invalid |

  Scenario Outline: fetch item after creation
    Given delete existing items
    And item properties are <id> <title_id>
    And create current item
    Then fetching item <id> should be "<status>"
    Examples:
      | id | title_id | status |
      | 1  | 1        | valid  |
      | 2  | 1        | valid  |

  Scenario Outline: delete item without creation
    Given delete existing items
    Then deleting item <id> should be "<status>"
    Examples:
      | id | status  |
      | 1  | invalid |
      | 2  | invalid |

  Scenario Outline: delete item after creation
    Given delete existing items
    And item properties are <id> <title_id>
    And create current item
    Then deleting item <id> should be "<status>"
    Examples:
      | id | title_id | status |
      | 1  | 1        | valid  |
      | 2  | 1        | valid  |
         
