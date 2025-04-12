package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */

public class AdminHomePage {
	
	private final DatabaseHelper databaseHelper;
	
	public AdminHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
	
	/**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage, String userName) {
    	VBox layout = new VBox(5);
    	
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    Button logoutButton = new Button("Back to Home Page");
	    logoutButton.setOnAction(a -> {
	    	new HomePageSelectionPage(databaseHelper).show(primaryStage, userName);
        });
	    layout.getChildren().add(logoutButton);
	    
	    // label to display the welcome message for the admin
	    Label adminLabel = new Label("Your Admin Page");
	    adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    layout.getChildren().add(adminLabel);
	    
	    Button changeInfoButton = new Button("Change Personal Info");
	    changeInfoButton.setOnAction(a -> {
	    	new ChangeInfoPage(databaseHelper).show(primaryStage, userName);
        });
	    layout.getChildren().add(changeInfoButton);
	    
	    Button viewMessagesButton =  new Button("View Private Messages");
	    viewMessagesButton.setOnAction(a -> {	
			new InboxPage(databaseHelper).show(primaryStage, userName);
	    });
	    layout.getChildren().add(viewMessagesButton);
	    
	    Button roleAlterationButton = new Button("Edit User Roles");
	    roleAlterationButton.setOnAction(a -> {
            new RoleAlterationPage().show(databaseHelper, primaryStage, userName);
        });
	    
	    Button deleteButton = new Button("Delete Users");
	    deleteButton.setOnAction(a -> {
            new DeleteUserPage().show(databaseHelper, primaryStage, userName);
        });
	    
        Button inviteButton = new Button("Invite Users");
        inviteButton.setOnAction(a -> {
            new InvitationPage().show(databaseHelper, primaryStage, userName);
        });
        
        Button passwordButton = new Button("Set One-Time Password");
        passwordButton.setOnAction(a -> {
            new OneTimePasswordPage().show(databaseHelper, primaryStage, userName);
        });
        
        Label userListLabel = new Label("");
        Button userListButton = new Button("View User Information");
        userListButton.setOnAction(a -> {
            new UserListPage().show(databaseHelper, primaryStage, userName);
        });

	    layout.getChildren().addAll(roleAlterationButton, inviteButton, deleteButton, passwordButton, userListButton, userListLabel);

	    Scene adminScene = new Scene(layout, 1200, 600);

	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Admin Page");
    }
}