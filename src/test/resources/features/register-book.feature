@section-Book
Feature: Register a book
  In order to be an attractive library
  As a librarian
  I want to register books

  Scenario: Book does not exist
    Given already registered books:
      | Harry Potter    |
      | Game of thrones |
    When registering the book Elantris
    Then this book should be registered:
      | isbn       | title    | author             |
      | 2253159913 | Elantris | Sanderson, Brandon |


  Scenario: Book already registered with same ISBN
    Given already registered books:
      | Harry Potter    |
      | Game of thrones |
    When registering the book Harry Potter
    Then the book registration should fail because a book with same ISBN is already registered
