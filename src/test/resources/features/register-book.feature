Feature: Register a book
  In order to be an attractive library
  As a librarian
  I want to register books


  Scenario: Book does not exist
    Given already registered books:
      | isbn       | title                                    | author               |
      | 0747532745 | Harry Potter and the Philosopher's Stone | Rowling, J. K.       |
      | 0553897845 | A Song of Ice and Fire                   | Martin, George R. R. |

    When registering new book:
      | isbn       | title    | author             |
      | 2253159913 | Elantris | Sanderson, Brandon |

    Then this book should be registered:
      | isbn       | title    | author             |
      | 2253159913 | Elantris | Sanderson, Brandon |


  Scenario: Book already registered with same ISBN
    Given already registered books:
      | isbn       | title                                    | author               |
      | 0747532745 | Harry Potter and the Philosopher's Stone | Rowling, J. K.       |
      | 0553897845 | A Song of Ice and Fire                   | Martin, George R. R. |

    When registering new book:
      | isbn       | title        | author  |
      | 0553897845 | Harry Potter | Rowling |

    Then this book should be registered:
      | isbn       | title                                    | author         |
      | 0747532745 | Harry Potter and the Philosopher's Stone | Rowling, J. K. |

    And the book registration result should be an error indicating a book with same ISBN is already registered
