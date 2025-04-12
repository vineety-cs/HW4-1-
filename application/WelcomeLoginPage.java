package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.*;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
public class WelcomeLoginPage {
	
	private final DatabaseHelper databaseHelper;

    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    public void show(Stage primaryStage, String userName) {
    	
    	VBox layout = new VBox(5);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    Button logoutButton = new Button("Log Out");
	    logoutButton.setOnAction(a -> {
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
	    
	    Label welcomeLabel = new Label("Welcome!!");
	    welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Button to navigate to the user's respective page based on their role
	    Button continueButton = new Button("Continue to Home Page");
	    continueButton.setOnAction(a -> {
	    	String[] roles = databaseHelper.getUserRoles(userName);
	    	
	    	if (roles[0] == "" || roles[0] == null) {
	    		new UserHomePage(databaseHelper).show(primaryStage, userName);
	    	}
//	    	else if (roles[1] == "" || roles[1] == null) {
//	    		switch(roles[0]) {
//	    		case "Admin": new AdminHomePage(databaseHelper).show(primaryStage, userName); break;
//	    		case "Student": new StudentHomePage(databaseHelper).show(primaryStage, userName); break;
//	    		case "Staff": new StaffHomePage(databaseHelper).show(primaryStage, userName); break;
//	    		case "Instructor": new InstructorHomePage(databaseHelper).show(primaryStage, userName); break;
//	    		case "Reviewer": new ReviewerHomePage(databaseHelper).show(primaryStage, userName); break;
//	    		}
//	    	}
	    	else {
	    		new HomePageSelectionPage(databaseHelper).show(primaryStage, userName);
	    	}
	    });

	    layout.getChildren().addAll(welcomeLabel, continueButton, logoutButton);
	    Scene welcomeScene = new Scene(layout, 1200, 600);

	    // Set the scene to primary stage
	    primaryStage.setScene(welcomeScene);
	    primaryStage.setTitle("Welcome Page");
    }
}