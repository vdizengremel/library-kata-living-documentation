Feature: Get book by ISBN
  In order to get book information
  As a librarian
  I want to get book by ISBN

  Scenario: Book exists
    Given already registered books:
      | isbn       | title                                    | author               |
      | 2253159913 | Elantris                                 | Sanderson, Brandon   |
      | 0747532745 | Harry Potter and the Philosopher's Stone | Rowling, J. K.       |
      | 0553897845 | A Song of Ice and Fire                   | Martin, George R. R. |
    When getting book with ISBN 0747532745
    Then the following book should be returned:
      | isbn       | title                                    | author         |
      | 0747532745 | Harry Potter and the Philosopher's Stone | Rowling, J. K. |

  Scenario: Book does not exist
    Given already registered books:
      | isbn       | title                                    | author               |
      | 2253159913 | Elantris                                 | Sanderson, Brandon   |
    When getting book with ISBN 0747532745
    Then the returning of book should fail because book does not exist
