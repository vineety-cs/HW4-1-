package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import databasePart1.*;

public class ChangeInfoPage {
	
    private final DatabaseHelper databaseHelper;
    // DatabaseHelper to handle database operations.
    public ChangeInfoPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage, String userName) {

    	Label userLabel = new Label("Hello, " + userName + ".");
    	
    	Label userNameContainer = new Label(userName);
    	userNameContainer.setVisible(false);
    	userNameContainer.setManaged(false);
    	
        // Label to display error messages for invalid input or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
    	
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Username");
        userNameField.setMaxWidth(250);
        
        Label userNameLabel = new Label("Username is taken!");
        userNameLabel.setManaged(false);
        userNameLabel.setVisible(false);
        
        // Listener will be used to update userName availability in real time
        userNameField.textProperty().addListener((observable, oldValue, newValue) -> {
        	// if passwordEvaluator does not return empty, display error
        	boolean evaluation = databaseHelper.doesUserExist(newValue);
            userNameLabel.setManaged(evaluation);
            userNameLabel.setVisible(evaluation);
        });
        
        Button userNameButton = new Button ("Change Username");
        userNameButton.setOnAction(a ->{
        	String newUserName = userNameField.getText();
        	String userNameErrorMessage = UserNameRecognizer.checkForValidUserName(newUserName);
        	if (userNameErrorMessage == "" && !databaseHelper.doesUserExist(newUserName)) {
        		try {
					errorLabel.setText(databaseHelper.setUserName(userNameContainer.getText(), newUserName));
					if (errorLabel.getText().contains("has been given the username")) userNameContainer.setText(newUserName);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	else errorLabel.setText(userNameErrorMessage);
        });
        
        TextField emailField = new TextField();
        emailField.setPromptText("Enter New Email");
        emailField.setMaxWidth(250);
        
        Button emailButton = new Button ("Change Email");
        emailButton.setOnAction(a ->{
        	String email = emailField.getText();
        	String emailErrorMessage = EmailParser.checkForValidEmail(email);
        	if (emailErrorMessage == "") {
        		try {
					errorLabel.setText(databaseHelper.setEmail(userNameContainer.getText(), email));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	else errorLabel.setText(emailErrorMessage);
        });
        
        TextField nameField = new TextField();
        nameField.setPromptText("Enter New Name");
        nameField.setMaxWidth(250);
        
        Button nameButton = new Button ("Change Name");
        nameButton.setOnAction(a ->{
        	String name = nameField.getText();
        	try {
				errorLabel.setText(databaseHelper.setName(userNameContainer.getText(), name));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        });

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

	    Button backButton = new Button("Back");
	    backButton.setOnAction(a -> {
	    	new HomePageSelectionPage(databaseHelper).show(primaryStage, userName);
        }); 
        
        
        Button passwordButton = new Button("Change Password");
        passwordButton.setOnAction(a -> {
        	// Retrieve user input
            String password = passwordField.getText();
            String passwordErrorMessage = PasswordEvaluator.evaluatePassword(password);
            if (!(password.equals(verifyPasswordField.getText()))) passwordErrorMessage = "Passwords do not match.\n" + passwordErrorMessage;
            errorLabel.setText(passwordErrorMessage);
            
            if (passwordErrorMessage == "") { // ensure password is valid
            	try {
            		if (databaseHelper.setPassword(userNameContainer.getText(), password, false).equals("SQL Error")) {
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
        
        Button viewInfoButton = new Button("View Account Information");
        viewInfoButton.setOnAction(a -> {
            try {
				errorLabel.setText(databaseHelper.getNewUserInfo(userNameContainer.getText()));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        });
        
        VBox layout = new VBox(5);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(backButton, userLabel, userNameContainer, userNameField, userNameLabel, userNameButton, nameField, nameButton, emailField, emailButton, passwordField, passwordTextField, passwordStrengthBar, passwordStrengthLabel, verifyPasswordField, verifyPasswordTextField, showPasswordCheckBox, passwordButton, viewInfoButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 1200, 600));
        primaryStage.setTitle("Account Information");
        primaryStage.show();
    }
}
