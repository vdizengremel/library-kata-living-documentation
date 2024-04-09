@section-Member
Feature: Register a member
  In order to be a library member and be allowed to borrow books
  As a person
  I want to register

  Scenario: Registration with new email
    Given Jane is a registered member
    And John is a registered member
    And Matt is a registered member
    When a person registers with information:
      | firstname | lastname | email                |
      | Jean      | Dupond   | jean.dupond@smth.com |
    Then last registered member should be:
      | firstname | lastname | email                |
      | Jean      | Dupond   | jean.dupond@smth.com |

  Scenario: Registration with already known email
    Given Matt is a registered member
    When a person registers with information:
      | firstname | lastname | email         |
      | Matt      | Lejaune  | matt@acme.com |
    Then the member registration should fail because a member with same email exists
    And Matt should have not changed
