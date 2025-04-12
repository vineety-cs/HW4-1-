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

public class HomePageSelectionPage {
	
	private final DatabaseHelper databaseHelper;
	
	public HomePageSelectionPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
	
	/**
     * Displays the selection page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     * @param roles The roles that the user has to choose between.
     */
    public void show(Stage primaryStage, String userName) {
    	VBox layout = new VBox(5);
    	
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // label to display the welcome message for the admin
	    Label selectLabel = new Label("Welcome to your Home Page! Select a Role to Play:");
	    
	    Button logoutButton = new Button("Log Out");
	    logoutButton.setOnAction(a -> {
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
	    
        Button adminButton = new Button("Admin Page");
        adminButton.setOnAction(a -> {
            new AdminHomePage(databaseHelper).show(primaryStage, userName);
        });
        
        Button studentButton = new Button("Student Page");
        studentButton.setOnAction(a -> {
            new StudentHomePage(databaseHelper).show(primaryStage, userName);
        });
        
        Button staffButton = new Button("Staff Page");
        staffButton.setOnAction(a -> {
            new StaffHomePage(databaseHelper).show(primaryStage, userName);
        });
        
        Button instructorButton = new Button("Instructor Page");
        instructorButton.setOnAction(a -> {
            new InstructorHomePage(databaseHelper).show(primaryStage, userName);
        });
        
        Button reviewerButton = new Button("Reviewer Page");
        reviewerButton.setOnAction(a -> {
            new ReviewerHomePage(databaseHelper).show(primaryStage, userName);
        });
        
        layout.getChildren().addAll(logoutButton, selectLabel);
        
        
        
        String[] roles = databaseHelper.getUserRoles(userName);
        
        if (roles[0] == "" || roles[0] == null) {
    		new UserHomePage(databaseHelper).show(primaryStage, userName);
    	}
    	else if (roles[1] == "" || roles[1] == null) {
    		switch(roles[0]) {
    		case "Admin": new AdminHomePage(databaseHelper).show(primaryStage, userName); break;
    		case "Student": new StudentHomePage(databaseHelper).show(primaryStage, userName); break;
    		case "Staff": new StaffHomePage(databaseHelper).show(primaryStage, userName); break;
    		case "Instructor": new InstructorHomePage(databaseHelper).show(primaryStage, userName); break;
    		case "Reviewer": new ReviewerHomePage(databaseHelper).show(primaryStage, userName); break;
    		}
    	}
        for (int i = 0; i < roles.length; i++) {
        	switch (roles[i]) {
        		case null: break;
        		case "Admin": layout.getChildren().add(adminButton); break;
        		case "Student": layout.getChildren().add(studentButton); break;
        		case "Staff": layout.getChildren().add(staffButton); break;
        		case "Instructor": layout.getChildren().add(instructorButton); break;
        		case "Reviewer": layout.getChildren().add(reviewerButton); break;
        		default: break;
        	}
        }
        
	    
	    selectLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    
	    Scene selectScene = new Scene(layout, 1200, 600);

	    // Set the scene to primary stage
	    primaryStage.setScene(selectScene);
	    primaryStage.setTitle("Select Role to Play");
    }
}