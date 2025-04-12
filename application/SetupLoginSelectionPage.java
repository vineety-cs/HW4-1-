package application;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import databasePart1.*;

/**
 * The SetupLoginSelectionPage class allows users to choose between setting up a new account
 * or logging into an existing account. It provides two buttons for navigation to the respective pages.
 */
public class SetupLoginSelectionPage {
	
    private final DatabaseHelper databaseHelper;

    public SetupLoginSelectionPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        
    	// Buttons to select Login / Setup options that redirect to respective pages
        Button registerButton = new Button("Register");
        Button loginButton = new Button("Login");
	    // Button to quit the application
	    Button quitButton = new Button("Quit");
        
        registerButton.setOnAction(a -> {
            new SetupAccountPage(databaseHelper).show(primaryStage);
        });
        loginButton.setOnAction(a -> {
        	new UserLoginPage(databaseHelper).show(primaryStage);
        });
	    quitButton.setOnAction(a -> {
	    	databaseHelper.closeConnection();
	    	Platform.exit(); // Exit the JavaFX application
	    });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(loginButton, registerButton, quitButton);

        primaryStage.setScene(new Scene(layout, 1200, 600));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}
