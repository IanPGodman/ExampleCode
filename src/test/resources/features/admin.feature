Feature: The admin user can use the admin page

    Scenario: Admin user can make another user an admin
        Given that the test container is running
        Then the user Test can not access the admin page
        And logs out
        Then the Amin user logs in
        And can access the admin page
        And can give the user Test admin rights
        Then the Admin user logs out
        Then the user Test logs in
        And can access the admin page