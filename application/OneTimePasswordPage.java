package application;


import java.sql.SQLException;
import java.util.UUID;

import databasePart1.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class OneTimePasswordPage {
    public void show(DatabaseHelper databaseHelper, Stage primaryStage, String userName) {
    	VBox layout = new VBox(5);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    Button backButton = new Button("Back");
	    backButton.setOnAction(a -> {
	    	new AdminHomePage(databaseHelper).show(primaryStage, userName);
        });
	    
    	// Input field for the user's userName
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);
	    
	    // Label to display the title of the page
	    Label userLabel = new Label("Generate and Assign Temporary Passwords");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Label to display the result of the operation
	    Label resultLabel = new Label(""); ;
	    
	    // Button to generate and assign the password
	    Button setPasswordButton = new Button("Generate One-Time Password");        
	    setPasswordButton.setOnAction(a -> {
        	try {
        		if (userNameField.getText() == userName) {
        			resultLabel.setText("Error: to change your own password, go back to your Home Page to the Account Information Page.");
        		}
        		else if (databaseHelper.doesUserExist(userNameField.getText())) { 
        			resultLabel.setText(databaseHelper.setPassword(userNameField.getText(), UUID.randomUUID().toString().substring(0, 12), true));
        		}
        		else {
        			resultLabel.setText("User does not exist");
        		}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        });

        layout.getChildren().addAll(backButton, userLabel, userNameField, setPasswordButton, resultLabel);
	    Scene oneTimePasswordScene = new Scene(layout, 1200, 600);

	    // Set the scene to primary stage
	    primaryStage.setScene(oneTimePasswordScene);
	    primaryStage.setTitle("One-Time Password Page");
    	
    }
}