package application;


import databasePart1.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * InvitePage class represents the page where an admin can generate an invitation code.
 * The invitation code is displayed upon clicking a button.
 */

public class InvitationPage {
	
	boolean admin = false, student = false, staff = false, instructor = false, reviewer = false;

	/**
     * Displays the Invite Page in the provided primary stage.
     * 
     * @param databaseHelper An instance of DatabaseHelper to handle database operations.
     * @param primaryStage   The primary stage where the scene will be displayed.
     */
    public void show(DatabaseHelper databaseHelper, Stage primaryStage, String userName) {
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    Button backButton = new Button("Back");
	    backButton.setOnAction(a -> {
	    	new AdminHomePage(databaseHelper).show(primaryStage, userName);
        });
	    
	    // Label to display the title of the page
	    Label userLabel = new Label("Add Roles to Invite Code");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    Label buttonLabel = new Label("Code will have these roles: None");
	    
        Button adminButton = new Button("Admin");
        adminButton.setOnAction(a -> {
        	admin = !admin;
            buttonLabel.setText("Code will have these roles: " + updateButton());
        });
        
        Button studentButton = new Button("Student");
        studentButton.setOnAction(a -> {
        	student = !student;
        	buttonLabel.setText("Code will have these roles: " + updateButton());
        });
        
        Button staffButton = new Button("Staff");
        staffButton.setOnAction(a -> {
        	staff = !staff;
        	buttonLabel.setText("Code will have these roles: " + updateButton());
        });
        
        Button instructorButton = new Button("Instructor");
        instructorButton.setOnAction(a -> {
        	instructor = !instructor;
        	buttonLabel.setText("Code will have these roles: " + updateButton());
        });
        
        Button reviewerButton = new Button("Reviewer");
        reviewerButton.setOnAction(a -> {
        	reviewer = !reviewer;
        	buttonLabel.setText("Code will have these roles: " + updateButton());
        });
	    
	    // Button to generate the invitation code
	    Button showCodeButton = new Button("Generate Invitation Code");
	    
	    // Label to display the generated invitation code
	    Label inviteCodeLabel = new Label(""); ;
        inviteCodeLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
        
        showCodeButton.setOnAction(a -> {
        	// Generate the invitation code using the databaseHelper and set it to the label
            String invitationCode = databaseHelper.generateInvitationCode(updateButton());
            inviteCodeLabel.setText(invitationCode);
        });
	    

        layout.getChildren().addAll(backButton, userLabel, adminButton, studentButton, staffButton, instructorButton, reviewerButton, buttonLabel, showCodeButton, inviteCodeLabel);
	    Scene inviteScene = new Scene(layout, 1200, 600);

	    // Set the scene to primary stage
	    primaryStage.setScene(inviteScene);
	    primaryStage.setTitle("Invite Page");
    	
    }
    
    private String updateButton() {
    	
    	String[] roles = new String[8];
    	User user = new User("username", "name", "email", "password", roles);
    	
    	if (admin) user.addRole("Admin");
    	if (student) user.addRole("Student");
    	if (staff) user.addRole("Staff");
    	if (instructor) user.addRole("Instructor");
    	if (reviewer) user.addRole("Reviewer");
    	
    	String newLabel = user.getRoleList();
    	newLabel = ((newLabel == null || newLabel.equals("")) ? "None" : newLabel);
    	return newLabel;
    }
}