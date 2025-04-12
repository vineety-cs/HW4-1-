package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class PasswordResetPage {
	
    private final DatabaseHelper databaseHelper;
    // DatabaseHelper to handle database operations.
    public PasswordResetPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage, String userName) {
    	
        Label userNameLabel = new Label("Hello " + userName + ", please enter a new password:");

        // Password Hidden
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);        
        
        // Password Shown
        TextField passwordTextField = new TextField();
        passwordTextField.setPromptText("Enter Password");
        passwordTextField.setMaxWidth(250);
        passwordTextField.setManaged(false);
        passwordTextField.setVisible(false);
        
        // Connect two password fields
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
        
        // Password Hidden
        PasswordField verifyPasswordField = new PasswordField();
        verifyPasswordField.setPromptText("Verify Password");
        verifyPasswordField.setMaxWidth(250);        
        
        // Password Shown
        TextField verifyPasswordTextField = new TextField();
        verifyPasswordTextField.setPromptText("Verify Password");
        verifyPasswordTextField.setMaxWidth(250);
        verifyPasswordTextField.setManaged(false);
        verifyPasswordTextField.setVisible(false);
        
        // Connect two password fields
        verifyPasswordTextField.textProperty().bindBidirectional(verifyPasswordField.textProperty());
        
        // CheckBox to toggle password visibility
        CheckBox showPasswordCheckBox = new CheckBox("Show Password");
        showPasswordCheckBox.setOnAction(e -> {
    		passwordTextField.setManaged(showPasswordCheckBox.isSelected());
            passwordTextField.setVisible(showPasswordCheckBox.isSelected());
            passwordField.setManaged(!showPasswordCheckBox.isSelected());
            passwordField.setVisible(!showPasswordCheckBox.isSelected());
        	verifyPasswordTextField.setManaged(showPasswordCheckBox.isSelected());
        	verifyPasswordTextField.setVisible(showPasswordCheckBox.isSelected());
        	verifyPasswordField.setManaged(!showPasswordCheckBox.isSelected());
        	verifyPasswordField.setVisible(!showPasswordCheckBox.isSelected());
        });

        // Label for password strength
        Label passwordStrengthLabel = new Label();

        // Bar for password strength
        ProgressBar passwordStrengthBar = new ProgressBar(0);
        passwordStrengthBar.setPrefWidth(250);

        // Listener will be used to update strength meter in real time
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
        	// if passwordEvaluator does not return empty, display error
        	String evaluation = PasswordEvaluator.evaluatePassword(newValue);
        	if (!evaluation.isEmpty()) {
        		passwordStrengthLabel.setText(evaluation);
        		passwordStrengthBar.setProgress(0);
        	} else {
        		// all password requirements met
        		int length = newValue.length();
        		// password strength equation: weak <= 10 char, medium <= 15 char, strong > 15 char
        		double progress = Math.min(1.0,  (length - 7) / 10.0);
        		passwordStrengthBar.setProgress(progress);

        		if (progress < 0.4) {
        			passwordStrengthLabel.setText("Weak");
        		} else if (progress < 0.7) {
        			passwordStrengthLabel.setText("Medium");
        		} else {
        			passwordStrengthLabel.setText("Strong");
        		}
        	}
        });
        
        // Label to display error messages for invalid input or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

    	Button logoutButton = new Button("Log Out");
	    logoutButton.setOnAction(a -> {
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
        Button setupButton = new Button("Set Password");
        
        setupButton.setOnAction(a -> {
        	// Retrieve user input
            String password = passwordField.getText();
            String passwordErrorMessage = PasswordEvaluator.evaluatePassword(password);
            if (!(password.equals(verifyPasswordField.getText()))) passwordErrorMessage = "Passwords do not match.\n" + passwordErrorMessage;
            errorLabel.setText(passwordErrorMessage);
            
            if (passwordErrorMessage == "") { // ensure password is valid
            	try {
            		if (databaseHelper.setPassword(userName, password, false).equals("SQL Error")) {
            			errorLabel.setText("Error changing password");
            		}
            		else {
            			errorLabel.setText("Password Set Successfully");
            		}
            	} catch (SQLException e) {
                	System.err.println("Database error: " + e.getMessage());
                	e.printStackTrace();
            	}
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameLabel, passwordField, passwordTextField, passwordStrengthBar, passwordStrengthLabel, verifyPasswordField, verifyPasswordTextField, showPasswordCheckBox, setupButton, logoutButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 1200, 600));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}
