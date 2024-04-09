@subsection-Book
Feature: Get book by ISBN
  In order to get book information
  As a librarian
  I want to get book by ISBN

  @happy-path
  Scenario: Book exists
    Given already registered books:
      | Elantris        |
      | Harry Potter    |
      | Game of thrones |
    When getting book with ISBN 0747532745
    Then the following book should be returned:
      | isbn       | title                                    | author         |
      | 0747532745 | Harry Potter and the Philosopher's Stone | Rowling, J. K. |

  Scenario: Book does not exist
    Given already registered books:
      | Elantris |
    When getting book with ISBN 0747532745
    Then the returning of book should fail because book does not exist
