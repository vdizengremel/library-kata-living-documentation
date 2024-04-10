Feature: POST
  Scenario: Registering book
    When posting to /book
    Then the response status code should be 200
