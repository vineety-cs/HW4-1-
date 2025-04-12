package application;

import databasePart1.DatabaseHelper;
import java.sql.SQLException;

public class AutomatedTestingSuite {

    static int numPassed = 0;
    static int numFailed = 0;

    public static void main(String[] args) {
        System.out.println("______________________________");
        System.out.println("\nAutomated Testing Suite\n");

        DatabaseHelper dbHelper = new DatabaseHelper();
        try {
            dbHelper.connectToDatabase();

            // Register TestUser if not exists
            if (!dbHelper.doesUserExist("TestUser")) {
                dbHelper.register(new User("TestUser", "Test Name", "testuser@example.com", "TestPass1!", new String[]{"Student", "Admin", "Staff", "Instructor", "Reviewer"}));
                System.out.println("TestUser registered successfully.");
            }

            // List of Automated Tests
            System.out.println("\nEstablishing and Documenting New Automated Tests:\n");
            System.out.println("1. Username Validation Tests");
            System.out.println("2. Password Validation Tests");
            System.out.println("3. Email Input Validation Tests");
            System.out.println("4. Role Validation Tests");
            System.out.println("5. One-Time Code Validation Tests\n");

            // Username Validation Tests
            performTestCase("Username Test 1", "ValidUser123", true, UserNameRecognizer::checkForValidUserName);
            performTestCase("Username Test 2", "Us", false, UserNameRecognizer::checkForValidUserName);
            performTestCase("Username Test 3", "user.name", true, UserNameRecognizer::checkForValidUserName);
            performTestCase("Username Test 4", "1InvalidUser", false, UserNameRecognizer::checkForValidUserName);

            // Password Validation Tests
            performTestCase("Password Test 1", "StrongPass1!", true, PasswordEvaluator::evaluatePassword);
            performTestCase("Password Test 2", "weak", false, PasswordEvaluator::evaluatePassword);
            performTestCase("Password Test 3", "NoSpecialChar123", false, PasswordEvaluator::evaluatePassword);
            performTestCase("Password Test 4", "NoNumber!", false, PasswordEvaluator::evaluatePassword);

            // Email Validation Tests
            performTestCase("Email Test 1", "user@example.com", true, EmailParser::checkForValidEmail);
            performTestCase("Email Test 2", "invalid-email", false, EmailParser::checkForValidEmail);
            performTestCase("Email Test 3", "user.name@domain.co", true, EmailParser::checkForValidEmail);
            performTestCase("Email Test 4", "@missingusername.com", false, EmailParser::checkForValidEmail);

            // Role Validation Tests
            performRoleTestCase("Role Test 1", "Student", true);
            performRoleTestCase("Role Test 2", "InvalidRole", false);

            // One-Time Code Validation Tests
            performOneTimeCodeTestCase("OTP Test 1", "1234567890", true);
            performOneTimeCodeTestCase("OTP Test 2", "expiredcode", false);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbHelper.closeConnection();
        }

        System.out.println("__________________________________________________________________");
        System.out.println("Number of Tests Passed: " + numPassed);
        System.out.println("Number of Tests Failed: " + numFailed);

        System.out.println("\nAddressing Issues Identified During Testing:\n");
        System.out.println("- Fix validation logic for usernames starting with digits.");
        System.out.println("- Enhance password validation to catch missing special characters.");
        System.out.println("- Improve email regex to support edge cases like subdomains.");
        System.out.println("- Ensure role validation rejects unlisted roles correctly.");
        System.out.println("- Adjust one-time code logic to handle expired codes effectively.");
    }

    private static void performTestCase(String testName, String input, boolean expectedPass, ValidationFunction validator) {
        System.out.println("\nRunning Test: " + testName);
        String result = validator.validate(input);

        if ((result.isEmpty() && expectedPass) || (!result.isEmpty() && !expectedPass)) {
            System.out.println("*** SUCCESS *** Test Passed");
            numPassed++;
        } else {
            System.out.println("*** FAILURE *** Test Failed");
            System.out.println("Input: " + input);
            System.out.println("Expected: " + (expectedPass ? "Valid" : "Invalid"));
            System.out.println("Actual: " + (result.isEmpty() ? "Valid" : result));
            numFailed++;
        }
    }

    private static void performRoleTestCase(String testName, String role, boolean expectedPass) {
        System.out.println("\nRunning Test: " + testName);
        DatabaseHelper dbHelper = new DatabaseHelper();
        boolean isValidRole = false;

        try {
            dbHelper.connectToDatabase();
            isValidRole = dbHelper.getUserRoleList("TestUser").contains(role);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbHelper.closeConnection();
        }

        if (isValidRole == expectedPass) {
            System.out.println("*** SUCCESS *** Test Passed");
            numPassed++;
        } else {
            System.out.println("*** FAILURE *** Test Failed");
            System.out.println("Role: " + role);
            numFailed++;
        }
    }

    private static void performOneTimeCodeTestCase(String testName, String code, boolean expectedPass) {
        System.out.println("\nRunning Test: " + testName);
        DatabaseHelper dbHelper = new DatabaseHelper();
        boolean isValidCode = false;

        try {
            dbHelper.connectToDatabase();
            isValidCode = dbHelper.validateInvitationCode(code);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbHelper.closeConnection();
        }

        if (isValidCode == expectedPass) {
            System.out.println("*** SUCCESS *** Test Passed");
            numPassed++;
        } else {
            System.out.println("*** FAILURE *** Test Failed");
            System.out.println("Code: " + code);
            numFailed++;
        }
    }

    @FunctionalInterface
    interface ValidationFunction {
        String validate(String input);
    }
}
