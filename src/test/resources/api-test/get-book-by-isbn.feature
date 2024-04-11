@section-Api
Feature: Get book by ISBN
  @happy-path
  Scenario: Book exists
    Given book Elantris is registered
    When GET /book/2253159913
    Then the response status code should be 200
    And the response body should be:
    """json
    {
      "isbn": "2253159913",
      "title": "Elantris",
      "author": "Sanderson, Brandon"
    }
    """

  Scenario: Book does not exist
    Given book is not registered yet
    When GET /book/0747532745
    Then the response status code should be 404
