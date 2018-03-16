Feature: test basic crud functionality on group entity

  Background:
    Given i go to "${admin.url}"
    And i fill in textbox "username" with "admin"
    And i fill in password "password" with "admin"
    And i click on button "/Sign in/"

  Scenario: see list of group entities
    Given i go to "${admin.url}/entities/group"
    Then i should see div "/60 groups gevonden./"
