@section-Member
Feature: Register a member
  In order to be a library member and be allowed to borrow books
  As a person
  I want to register

  Scenario: Registration with unknown email
    Given already registered members:
      | id                                   | firstname | lastname | email                  |
      | 4c502830-aca2-4c19-96c6-dd60959a1601 | Julien    | Durant   | julien.durant@smth.com |
      | 812f7cc4-a6e3-4035-aca5-df3774609170 | Melanie   | Versaire | melanie@smth.com       |
      | e0e50076-7c78-4344-97eb-4adf30e10a5c | Matt      | Legris   | matt@smth.com          |
    And next generated member ids will be:
      | 75c5f95e-efb6-4465-b12a-afd1c3ed8908 |
    When a person registers with information:
      | firstname | lastname | email                |
      | Jean      | Dupond   | jean.dupond@smth.com |
    Then a registered member should be:
      | id                                   | firstname | lastname | email                |
      | 75c5f95e-efb6-4465-b12a-afd1c3ed8908 | Jean      | Dupond   | jean.dupond@smth.com |

  Scenario: Registration with already known email
    Given already registered members:
      | id                                   | firstname | lastname | email                  |
      | 4c502830-aca2-4c19-96c6-dd60959a1601 | Julien    | Durant   | julien.durant@smth.com |
      | 812f7cc4-a6e3-4035-aca5-df3774609170 | Melanie   | Versaire | melanie@smth.com       |
      | e0e50076-7c78-4344-97eb-4adf30e10a5c | Matt      | Legris   | matt@smth.com          |
    When a person registers with information:
      | firstname | lastname | email         |
      | Matt      | Lejaune  | matt@smth.com |
    Then the member registration should fail because a member with same email exists
    And a registered member should be:
      | id                                   | firstname | lastname | email         |
      | e0e50076-7c78-4344-97eb-4adf30e10a5c | Matt      | Legris   | matt@smth.com |
