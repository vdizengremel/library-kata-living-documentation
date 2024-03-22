Feature: Register a member
  In order to be a library member and be allowed to borrow books
  As a person
  I want to register

  Scenario: Registration with unknown email
    Given following members exist:
      | firstname | lastname | email                  |
      | Julien    | Durant   | julien.durant@smth.com |
      | Melanie   | Versaire | melanie@smth.com       |
      | Matt      | Legris   | matt@smth.com          |
    When a person registers with information:
      | firstname | lastname | email                |
      | Jean      | Dupond   | jean.dupond@smth.com |
    Then last registered member should be:
      | firstname | lastname | email                |
      | Jean      | Dupond   | jean.dupond@smth.com |

  Scenario: Registration with already known email
    Given following members exist:
      | firstname | lastname | email                  |
      | Julien    | Durant   | julien.durant@smth.com |
      | Melanie   | Versaire | melanie@smth.com       |
      | Matt      | Legris   | matt@smth.com          |
    When a person registers with information:
      | firstname | lastname | email                |
      | Matt      | Lejaune   | matt@smth.com |
    Then the result should be an error indicating a member with same email exists
    And last registered member should be:
      | firstname | lastname | email                |
      | Matt      | Legris   | matt@smth.com |
