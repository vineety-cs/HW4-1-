package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPage {
	
    private final DatabaseHelper databaseHelper;

    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	
    	// Input field for the user's userName/email 
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Username or Email");
        userNameField.setMaxWidth(250);
        
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
        
        // CheckBox to toggle password visibility
        CheckBox showPasswordCheckBox = new CheckBox("Show Password");
        showPasswordCheckBox.setOnAction(e -> {
    		passwordTextField.setManaged(showPasswordCheckBox.isSelected());
            passwordTextField.setVisible(showPasswordCheckBox.isSelected());
            passwordField.setManaged(!showPasswordCheckBox.isSelected());
            passwordField.setVisible(!showPasswordCheckBox.isSelected());
        });
        
        // Label to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

	    Button backButton = new Button("Back");
	    backButton.setOnAction(a -> {
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        }); 
        
        Button loginButton = new Button("Login");
        
        loginButton.setOnAction(a -> {
            // Retrieve user inputs
            String userName = userNameField.getText();
            String password = passwordField.getText();
            try {
                if (userName.contains("@")) {
                    userName = databaseHelper.emailToUsername(userName);
                }
                
                // Create a temporary User for verifying the login credentials
                User user = new User(userName, "", "", password, null);
                
                if (userName.equals("USER NOT FOUND")) {
                    errorLabel.setText("User account doesn't exist");
                } else if (databaseHelper.doesUserExist(userName)) {
                    if (databaseHelper.login(user)) {
                        // Check if the user needs to reset their password
                        if (databaseHelper.resetPassword(user.getUserName())) {
                            new PasswordResetPage(databaseHelper).show(primaryStage, user.getUserName());
                        } else {
                            // Retrieve the full user details (including roles)
                            User fullUser = databaseHelper.getUser(userName);
                            
                            // Debug print to check roles (optional)
                            System.out.println("User roles: " + fullUser.getRoleList());
                            
                            // Check if the user has the "Staff" role
                            if (fullUser != null && fullUser.getRoleList().contains("Staff")) {
                                new StaffHomePage(databaseHelper).show(primaryStage, fullUser.getUserName());
                            } else {
                                new WelcomeLoginPage(databaseHelper).show(primaryStage, fullUser.getUserName());
                            }
                        }
                    } else {
                        errorLabel.setText("Incorrect password");
                    }
                } else {
                    errorLabel.setText("User account doesn't exist");
                }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });


        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(backButton, userNameField, passwordField, passwordTextField, showPasswordCheckBox, loginButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 1200, 600));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
}
