@section-Book
Feature: Return a book
  In order to keep borrowing books
  As a member
  I want to return borrowed books

  Scenario: Return book when book is borrowed
    Given current time is 2024-03-06
    And already registered members:
      | id                                   | firstname | lastname | email                  |
      | 4c502830-aca2-4c19-96c6-dd60959a1601 | Julien    | Durant   | julien.durant@smth.com |
    And next generated borrowing ids will be:
      | e0e50076-7c78-4344-97eb-4adf30e10a5c |
      | 75c5f95e-efb6-4465-b12a-afd1c3ed8908 |
    And already registered books:
      | isbn          | title                                    | author               |
      | 2253159913    | Elantris                                 | Sanderson, Brandon   |
      | 0747532745    | Harry Potter and the Philosopher's Stone | Rowling, J. K.       |
      | 0553897845    | A Song of Ice and Fire                   | Martin, George R. R. |
      | 9780425054710 | Dune                                     | Herbert, Frank       |
    And member with id 4c502830-aca2-4c19-96c6-dd60959a1601 has already borrowed following books at 2024-03-01 with ISBN:
      | 2253159913 |
      | 0553897845 |
    When the borrowed book with ISBN 2253159913 is returned
    Then borrowing with id e0e50076-7c78-4344-97eb-4adf30e10a5c should have return date 2024-03-06
    And borrowing with id 75c5f95e-efb6-4465-b12a-afd1c3ed8908 should have return date null

  Scenario: Try to return a book that is not borrowed
    When the borrowed book with ISBN 2253159913 is returned
    Then returning the book should fail because no borrowing exists for this book
