package application;


import java.sql.SQLException;

import databasePart1.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserListPage {

    public void show(DatabaseHelper databaseHelper, Stage primaryStage, String userName) {
    	VBox layout = new VBox(5);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    Button backButton = new Button("Back");
	    backButton.setOnAction(a -> {
	    	new AdminHomePage(databaseHelper).show(primaryStage, userName);
        });
	    
    	// Input field for the user's userName, password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);
	    
	    // Label to display the title of the page
	    Label userLabel = new Label("Access User Info");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Label to display the result of the operation
	    Label infoLabel = new Label("");
	    
	    // Button to display one user's information
	    Button userInfoButton = new Button("Get User's Info");        
	    userInfoButton.setOnAction(a -> {
	    	try {
            	infoLabel.setText(databaseHelper.getUserInfo(userNameField.getText()));
            	if (infoLabel.getText().equals("")) {
            		infoLabel.setText(userNameField.getText().equals("") ? "Error: please enter a username." : "User not found");
            	}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        });
	    
	    // Button to display all users' information
        Button userListButton = new Button("List All Users' Info");
        userListButton.setOnAction(a -> {
            try {
            	infoLabel.setText(databaseHelper.getUserList());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        });

        layout.getChildren().addAll(backButton, userLabel, userNameField, userInfoButton, userListButton, infoLabel);
	    Scene roleAlterationScene = new Scene(layout, 1200, 600);

	    // Set the scene to primary stage
	    primaryStage.setScene(roleAlterationScene);
	    primaryStage.setTitle("User Info Page");
    	
    }
}