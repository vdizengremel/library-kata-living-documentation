@subsection-Borrowing
@order-30
Feature: Borrow a book
  In order to read a book anywhere
  As a registered member
  I want to borrow a book

  Background:
    Given current time is 2024-03-06
    And Jane is a registered member
    And John is a registered member
    And already registered books:
      | Elantris        |
      | Harry Potter    |
      | Game of thrones |
      | Dune            |

  @happy-path
  Scenario: Member without borrowing can borrow book
  A book is borrowed for 3 weeks.

    When Jane borrows Elantris
    Then Jane should have current borrowings:
      | title    | start date | max authorized return date |
      | Elantris | 2024-03-06 | 2024-03-27                 |

  Scenario: Another member has reach number of authorized borrowing
  A book is borrowed for 3 weeks.
    Given John has already borrowed:
      | Dune            |
      | Game of thrones |
      | Harry Potter    |
    When Jane borrows Elantris
    Then Jane should have current borrowings:
      | title    | start date | max authorized return date |
      | Elantris | 2024-03-06 | 2024-03-27                 |
    And John should have current borrowings:
      | title           | start date | max authorized return date |
      | Dune            | 2024-03-06 | 2024-03-27                 |
      | Game of thrones | 2024-03-06 | 2024-03-27                 |
      | Harry Potter    | 2024-03-06 | 2024-03-27                 |

  Scenario: Member has reach number of authorized borrowing
  A member can have only 3 borrowings in progress.

    Given Jane has already borrowed:
      | Dune            |
      | Game of thrones |
      | Harry Potter    |
    When Jane borrows Elantris
    Then the borrowing should fail because member has reached the maximum amount of borrowed books

  Scenario: Member has not returned a book in time
    Given Jane has already borrowed following books at 2024-02-06:
      | Dune |
    When Jane borrows Elantris
    Then the borrowing should fail because member has not returned a book in time

  Scenario: Member does not exist
    When unknown member borrows Elantris
    Then the borrowing should fail because member does not exist

  Scenario: Book does not exist
    When Jane borrows an unknown book
    Then the borrowing should fail because book does not exist
