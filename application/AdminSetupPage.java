package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The SetupAdmin class handles the setup process for creating an administrator account.
 * This is intended to be used by the first user to initialize the system with admin credentials.
 */
public class AdminSetupPage {
	
    private final DatabaseHelper databaseHelper;

    public AdminSetupPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	
    	Label instructionsLabel = new Label("Enter account information. You will have to log in again once registered.");
    	
    	// Input fields for userName and password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Admin userName");
        userNameField.setMaxWidth(250);

        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email");
        emailField.setMaxWidth(250);
        
        TextField nameField = new TextField();
        nameField.setPromptText("Enter Name");
        nameField.setMaxWidth(250);
        
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
        
        
        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter InvitationCode");
        inviteCodeField.setMaxWidth(250);
        
	    Button backButton = new Button("Back");
	    backButton.setOnAction(a -> {
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        }); 
	    
        
        // Label to display error messages for invalid input or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        

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
        
        Button setupButton = new Button("Setup");
        
        setupButton.setOnAction(a -> {
        	// Retrieve user input
            String userName = userNameField.getText();
            String name = nameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            String[] roles = {"Admin"};
            
            // Evaluate user input 
            String userNameErrorMessage = UserNameRecognizer.checkForValidUserName(userName);
            String emailErrorMessage = EmailParser.checkForValidEmail(email);
            String passwordErrorMessage = PasswordEvaluator.evaluatePassword(password);
            if (!(password.equals(verifyPasswordField.getText()))) passwordErrorMessage = "Passwords do not match.\n" + passwordErrorMessage;
            
            if (userNameErrorMessage != "") { 				// Check if the userName is valid
            	errorLabel.setText(userNameErrorMessage);
            }
            else if (passwordErrorMessage != "") { 			//Check if the password is valid 
            	errorLabel.setText(passwordErrorMessage);
            }
            else if (emailErrorMessage != "") { 			//Check if the password is valid 
            	errorLabel.setText(emailErrorMessage);
            }
            else {
	            try {
	            	// Create a new User object and register in the database
	                databaseHelper.register(new User(userName, name, email, password, roles));
	                
	                Alert redirectAlert = new Alert(AlertType.INFORMATION, "Success! You will now be redirected to the login screen..");
	                redirectAlert.showAndWait().ifPresent(response -> {
	        		     new UserLoginPage(databaseHelper).show(primaryStage);
	        		});
	                
	                
	            } catch (SQLException e) {
	                System.err.println("Database error: " + e.getMessage());
	                e.printStackTrace();
	            }
        	}
        });

        VBox layout = new VBox(5, instructionsLabel, userNameField, nameField, emailField, passwordField, passwordTextField, passwordStrengthBar, passwordStrengthLabel, verifyPasswordField, verifyPasswordTextField, showPasswordCheckBox, setupButton, errorLabel);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        primaryStage.setScene(new Scene(layout, 1200, 600));
        primaryStage.setTitle("Administrator Setup");
        primaryStage.show();
    }
}
