package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {
	
    private final DatabaseHelper databaseHelper;
    // DatabaseHelper to handle database operations.
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	
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
        
        
        

        Button setupButton = new Button("Register Account");
        
        setupButton.setOnAction(a -> {
        	// Retrieve user input
            String userName = userNameField.getText();
            String userNameErrorMessage = UserNameRecognizer.checkForValidUserName(userName);
            String name = nameField.getText();
            
            String email = emailField.getText();
            String emailErrorMessage = EmailParser.checkForValidEmail(email);
            
            String password = passwordField.getText();
            String passwordErrorMessage = PasswordEvaluator.evaluatePassword(password);
            if (!(password.equals(verifyPasswordField.getText()))) passwordErrorMessage = "Passwords do not match.\n" + passwordErrorMessage;
            
            String code = inviteCodeField.getText();
            
            
            if (userNameErrorMessage != "") { 				// Check if the userName is valid
            	errorLabel.setText(userNameErrorMessage);
            }
            else if (emailErrorMessage != "") {
            	errorLabel.setText(emailErrorMessage);
            }
            else if (passwordErrorMessage != "") { 			//Check if the password is valid 
            	errorLabel.setText(passwordErrorMessage);
            }
            else {
            	try {
            		// Check if the user already exists
            		if(!databaseHelper.doesUserExist(userName)) {
            		
            			// Validate the invitation code
            			if(databaseHelper.validateInvitationCode(code)) {
            				String roleList = databaseHelper.getRoleListFromCode(code);
            				String[] roles = new String[8];
            				if (!(roleList == null || roleList == "" || roleList == "None")) {
	            				int n = 0;
	            				String currentRole = "";
	            				for (int i = 0; i < roleList.length(); i++) {
	            					if (roleList.charAt(i) == ',') {
	            						roles[n] = currentRole;
	            						currentRole = "";
	            						i++; n++;
	            						continue;
	            					}
	            					else {
	            						currentRole = currentRole + roleList.charAt(i);
	            					}
	            				}
	            				roles[n] = currentRole;
            				}
            				
            				// Create a new user and register them in the database
		                	databaseHelper.register(new User(userName, name, email, password, roles));
		                
		                	Alert redirectAlert = new Alert(AlertType.INFORMATION, "Success! You will now be redirected to the login screen..");
			                redirectAlert.showAndWait().ifPresent(response -> {
			        		     new UserLoginPage(databaseHelper).show(primaryStage);
			        		});
			                
            			}
            			else {
            				errorLabel.setText("Invalid or expired code");
            			}
            		}
            		else {
            			errorLabel.setText("Username is already taken");
            		}
            	
            	} catch (SQLException e) {
                	System.err.println("Database error: " + e.getMessage());
                	e.printStackTrace();
            	}
            }
        });

        VBox layout = new VBox(5);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(backButton, userNameField, userNameLabel, nameField, emailField, passwordField, passwordTextField, passwordStrengthBar, passwordStrengthLabel, verifyPasswordField, verifyPasswordTextField, showPasswordCheckBox, inviteCodeField, setupButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 1200, 600));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}
