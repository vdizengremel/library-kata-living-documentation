Feature: Borrow a book
  In order to read a book anywhere
  As a registered member
  I want to borrow a book

  Background:
    Given current time is 2024-03-06
    And next generated member ids will be:
      | 4c502830-aca2-4c19-96c6-dd60959a1601 |
    And already registered members:
      | firstname | lastname | email                  |
      | Julien    | Durant   | julien.durant@smth.com |
    And next generated borrowing ids will be:
      | e0e50076-7c78-4344-97eb-4adf30e10a5c |
      | 75c5f95e-efb6-4465-b12a-afd1c3ed8908 |
      | 75daf232-d971-4589-94e9-cf4f6df7edea |
      | be7c303e-7986-4e89-a699-2199b248c23f |
      | 2107d428-ed1a-4d97-9ee2-7b223e671f63 |
    And already registered books:
      | isbn          | title                                    | author               |
      | 2253159913    | Elantris                                 | Sanderson, Brandon   |
      | 0747532745    | Harry Potter and the Philosopher's Stone | Rowling, J. K.       |
      | 0553897845    | A Song of Ice and Fire                   | Martin, George R. R. |
      | 9780425054710 | Dune                                     | Herbert, Frank       |

  Scenario: Member has no borrowing
    When member with id 4c502830-aca2-4c19-96c6-dd60959a1601 borrow the book with ISBN 2253159913
    Then borrowing should be saved with:
      | id                                   | member id                            | isbn       | start date | max authorized return date |
      | e0e50076-7c78-4344-97eb-4adf30e10a5c | 4c502830-aca2-4c19-96c6-dd60959a1601 | 2253159913 | 2024-03-06 | 2024-03-27                 |

  Scenario: Member has reach number of authorized borrowing
    Given member with id 4c502830-aca2-4c19-96c6-dd60959a1601 has already borrowed following books with ISBN:
      | 9780425054710 |
      | 0747532745    |
      | 0553897845    |
    When member with id 4c502830-aca2-4c19-96c6-dd60959a1601 borrow the book with ISBN 2253159913
    Then the borrowing result should be an error indicating member has reached the maximum amount of borrowed books

  Scenario: Member has not returned a book in time
    Given member with id 4c502830-aca2-4c19-96c6-dd60959a1601 has already borrowed following books at 2024-02-06 with ISBN:
      | 9780425054710 |
    When member with id 4c502830-aca2-4c19-96c6-dd60959a1601 borrow the book with ISBN 2253159913
    Then the borrowing result should be an error indicating member has not returned a book in time

  Scenario: Member does not exist
    When member with id 11122233-1122-1122-aabb-aaaabbbbcccc borrow the book with ISBN 2253159913
    Then the borrowing result should be an error indicating member does not exist

  Scenario: Book does not exist
    When member with id 4c502830-aca2-4c19-96c6-dd60959a1601 borrow the book with ISBN 1112223333
    Then the borrowing result should be an error indicating book does not exist
