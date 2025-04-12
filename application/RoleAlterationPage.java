package application;


import java.sql.SQLException;

import databasePart1.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * InvitePage class represents the page where an admin can generate an invitation code.
 * The invitation code is displayed upon clicking a button.
 */

public class RoleAlterationPage {

	/**
     * Displays the Role Alteration Page in the provided primary stage.
     * 
     * @param databaseHelper An instance of DatabaseHelper to handle database operations.
     * @param primaryStage   The primary stage where the scene will be displayed.
     */
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
        
    	// Input field for the user's userName, password
        TextField roleField = new TextField();
        roleField.setPromptText("Enter role");
        roleField.setMaxWidth(250);
	    
	    // Label to display the title of the page
	    Label userLabel = new Label("Add/Remove Roles");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Label to display the result of the operation
	    Label resultLabel = new Label(""); ;
	    
	    // Button to add a role
	    Button addRoleButton = new Button("Add Role");        
	    addRoleButton.setOnAction(a -> {
        	try {
        		if (databaseHelper.doesUserExist(userNameField.getText())) { 
        			resultLabel.setText(databaseHelper.addRole(userNameField.getText(), roleField.getText()));
        		}
        		else {
        			resultLabel.setText("User does not exist");
        		}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        });
	    
	 // Button to remove a role
	    Button removeRoleButton = new Button("Remove Role");        
	    removeRoleButton.setOnAction(a -> {
        	try {
        		if (databaseHelper.doesUserExist(userNameField.getText())) { 
        			resultLabel.setText(databaseHelper.removeRole(userNameField.getText(), roleField.getText()));
        		}
        		else {
        			resultLabel.setText("User does not exist");
        		}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        });

        layout.getChildren().addAll(backButton, userLabel, userNameField, roleField, addRoleButton, removeRoleButton, resultLabel);
	    Scene roleAlterationScene = new Scene(layout, 1200, 600);

	    // Set the scene to primary stage
	    primaryStage.setScene(roleAlterationScene);
	    primaryStage.setTitle("Role Alteration Page");
    	
    }
}