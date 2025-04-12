package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class ReviewsPage {
    
    private final DatabaseHelper databaseHelper;
    
    public ReviewsPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    public void show(Stage primaryStage, String userName) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        Label titleLabel = new Label("My Reviews");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Area to display the user's questions
        TextArea reviewsArea = new TextArea();
        reviewsArea.setEditable(false);
        reviewsArea.setPrefHeight(200);
        
        // Retrieve questions from the database
        String questionsText;
        try {
            questionsText = databaseHelper.getUserReviews(userName);
        } catch (SQLException e) {
            questionsText = "Error retrieving questions: " + e.getMessage();
        }
        reviewsArea.setText(questionsText);
        
        // Button to return to the users view
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            new ReviewerHomePage(databaseHelper).show(primaryStage, userName);
        });
        
        layout.getChildren().addAll(titleLabel, new Label("Reviews:"), reviewsArea, backButton);
        
        Scene scene = new Scene(layout, 1200, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("My Reviews");
    }
    
}