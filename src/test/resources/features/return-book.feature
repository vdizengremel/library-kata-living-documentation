@subsection-Borrowing
Feature: Return a book
  In order to keep borrowing books
  As a member
  I want to return borrowed books

  @happy-path
  Scenario: Return book when book is borrowed
    Given current time is 2024-03-08
    And Jane is a registered member
    And already registered books:
      | Harry Potter    |
      | Game of thrones |
    And Jane has already borrowed following books at 2024-03-06:
      | Harry Potter    |
      | Game of thrones |
    When Harry Potter is returned
    Then Jane should have current borrowings:
      | title           | start date | max authorized return date |
      | Game of thrones | 2024-03-06 | 2024-03-27                 |
    And Jane should have past borrowings:
      | title        | start date | max authorized return date | return date |
      | Harry Potter | 2024-03-06 | 2024-03-27                 | 2024-03-08  |

  Scenario: Try to return a book that is not borrowed
    When Game of thrones is returned
    Then returning the book should fail because no borrowing exists for this book
