package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class SetReviewerPage {
    
    private final DatabaseHelper databaseHelper;
    
    public SetReviewerPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    public void show(Stage primaryStage, String targetUserName, String instructorUserName) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        Label titleLabel = new Label("User Q&A Overview: " + targetUserName);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Area to display the user's questions
        TextArea questionsArea = new TextArea();
        questionsArea.setEditable(false);
        questionsArea.setPrefHeight(200);
        
        // Area to display the user's answers
        TextArea answersArea = new TextArea();
        answersArea.setEditable(false);
        answersArea.setPrefHeight(200);
        
        // Retrieve questions from the database
        String questionsText;
        try {
            questionsText = databaseHelper.getUserQuestions(targetUserName);
        } catch (SQLException e) {
            questionsText = "Error retrieving questions: " + e.getMessage();
        }
        questionsArea.setText(questionsText);
        
        // Retrieve answers from the database
        String answersText;
        try {
            answersText = databaseHelper.getUserAnswers(targetUserName);
        } catch (SQLException e) {
            answersText = "Error retrieving questions: " + e.getMessage();
        }
        answersArea.setText(answersText);
        
        // Buttons for approving and revoking reviewer status
        Button approveButton = new Button("Approve Reviewer Status");
        Button revokeButton = new Button("Revoke Reviewer Status");
        Label statusLabel = new Label();
        
        approveButton.setOnAction(e -> {
            try {
                String result = databaseHelper.addRole(targetUserName, "Reviewer");
                statusLabel.setText("Reviewer status approved: " + result);
            } catch (SQLException ex) {
                statusLabel.setText("Error approving reviewer status: " + ex.getMessage());
            }
        });
        
        revokeButton.setOnAction(e -> {
            try {
                String result = databaseHelper.removeRole(targetUserName, "Reviewer");
                statusLabel.setText("Reviewer status revoked: " + result);
            } catch (SQLException ex) {
                statusLabel.setText("Error revoking reviewer status: " + ex.getMessage());
            }
        });
        
        // Button to return to the users view
        Button backButton = new Button("Back to User list");
        backButton.setOnAction(e -> {
            new InstructorUserListPage(databaseHelper).show(primaryStage, instructorUserName);
        });
        
        layout.getChildren().addAll(
            titleLabel, 
            new Label("Questions:"), questionsArea, 
            new Label("Answers:"), answersArea, 
            approveButton, revokeButton, 
            statusLabel, 
            backButton
        );
        
        Scene scene = new Scene(layout, 1200, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Instructor Review - " + targetUserName);
    }
    
}