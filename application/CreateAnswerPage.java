package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.UUID;

public class CreateAnswerPage {
    private final DatabaseHelper databaseHelper;
    private final Question question;
    private final String answerID;
    private boolean editing = false;
    
    // Create an answer page created by passing databaseHelper and question 
    public CreateAnswerPage(DatabaseHelper databaseHelper, Question q) {
        this.databaseHelper = databaseHelper;
        this.question = q;
        this.answerID = UUID.randomUUID().toString().substring(1);
    }
    
    // Create an answer page created by passing databaseHelper and question ID
    public CreateAnswerPage(DatabaseHelper databaseHelper, Question q, String aID) {
        this.databaseHelper = databaseHelper;
        this.question = q;
        this.answerID = aID;
        this.editing = true;
    }
    
    // Page is displayed by passing the stage and userName
    public void show(Stage stage, String userName) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        Label header = new Label("Create a New Answer");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        layout.getChildren().add(header);
        
        TextArea answerArea = new TextArea(editing ? databaseHelper.getAnswer(answerID).getAnswerText() : "");
        //answerArea.setPromptText("Enter your answer here...");
        answerArea.setWrapText(true);
        answerArea.setPrefHeight(150);
        layout.getChildren().add(answerArea);
        
        // Submit button for creating the answer
        Button submitButton = new Button("Submit Answer");
        submitButton.setOnAction(e -> {
            String answerText = answerArea.getText().trim();
            if (!answerText.isEmpty()) {
            	String result = "";
            	if (editing) databaseHelper.updateAnswer(answerID, answerText);
            	else result = databaseHelper.insertNewAnswer(answerID, question.getQuestionID(), userName, answerText, false);
				System.out.println(result);
                new AnswerListPage(databaseHelper, question).show(stage, userName);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Answer cannot be empty!", ButtonType.OK);
                alert.showAndWait();
            }
        });
        layout.getChildren().add(submitButton);
        
        // Back button to return without creating an answer
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            new AnswerListPage(databaseHelper, question).show(stage, userName);
        });
        layout.getChildren().add(backButton);
        
        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Create Answer for Question " + question.getContent());
        stage.show();
    }
}
