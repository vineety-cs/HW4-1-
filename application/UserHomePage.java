package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the user.
 */

public class UserHomePage {

	private final DatabaseHelper databaseHelper;
	
	public UserHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
	
    public void show(Stage primaryStage, String userName) {
    	VBox layout = new VBox(5);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    Button logoutButton = new Button("Log Out");
	    logoutButton.setOnAction(a -> {
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
	    layout.getChildren().add(logoutButton);
	    
	    // Label to display Hello user
	    Label userLabel = new Label("Your User Page");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    layout.getChildren().add(userLabel);
	    
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

	    Scene userScene = new Scene(layout, 1200, 600);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("User Page");
    	
    }
}