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

public class InstructorHomePage {

	private final DatabaseHelper databaseHelper;
	
	public InstructorHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
	
    public void show(Stage primaryStage, String userName) {
    	VBox layout = new VBox(5);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    Button logoutButton = new Button("Back to Home Page");
	    logoutButton.setOnAction(a -> {
	    	new HomePageSelectionPage(databaseHelper).show(primaryStage, userName);
        });
	    layout.getChildren().add(logoutButton);
	    
	    // Label to display Hello user
	    Label userLabel = new Label("Your Instructor Page");
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
	    
        Button viewUsersButton = new Button("View All Users");
        viewUsersButton.setOnAction(a -> {
            new InstructorUserListPage(databaseHelper).show(primaryStage, userName);
        });
        layout.getChildren().add(viewUsersButton);
        
        Button reviewRequestsButton = new Button("Review Role Requests");
        reviewRequestsButton.setOnAction(a -> {
            new ReviewRequestsPage(databaseHelper).show(primaryStage, userName);
        });
        layout.getChildren().add(reviewRequestsButton);
	    
	    Scene userScene = new Scene(layout, 1200, 600);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Instructor Page");
    	
    }
}