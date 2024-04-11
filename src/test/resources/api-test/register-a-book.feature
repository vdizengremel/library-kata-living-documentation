@section-Api
Feature: Register a book

  Background:
    Given request body is:
    """json
      {
          "isbn": "2070541274",
          "title": "Harry Potter",
          "author": "J. K. Rowling"
      }
    """

  @happy-path
  Scenario: Registering book succeed
    Given book is not registered yet
    When POST to /book/
    Then the response status code should be 204

  Scenario: Registering book failed
    Given book with same ISBN already exists
    When POST to /book/
    Then the response status code should be 400
    And the response body should be:
    """json
    {
        "reason": "Book already registered"
    }
    """
