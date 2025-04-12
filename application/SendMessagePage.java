package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.UUID;

public class SendMessagePage {
    private final DatabaseHelper databaseHelper;
    private final Question question;
    private final Answer answer;
    private final Review review;
    private final String messageID;
    
    
    public SendMessagePage(DatabaseHelper databaseHelper, Question q) {
        this.databaseHelper = databaseHelper;
        this.question = q;
        this.answer = null;
        this.review = null;
        this.messageID = UUID.randomUUID().toString().substring(3);
    }
    
    public SendMessagePage(DatabaseHelper databaseHelper, Review r, Question q) {
        this.databaseHelper = databaseHelper;
        this.question = q;
        this.answer = null;
        this.review = r;
        this.messageID = UUID.randomUUID().toString().substring(3);
    }
    
    public SendMessagePage(DatabaseHelper databaseHelper, Review r, Answer a) {
        this.databaseHelper = databaseHelper;
        this.question = null;
        this.answer = a;
        this.review = r;
        this.messageID = UUID.randomUUID().toString().substring(3);
    }
    
    // Page is displayed by passing the stage and userName
    public void show(Stage stage, String userName) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        Label header = new Label("Send a message:");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        layout.getChildren().add(header);
        
        TextArea messageArea = new TextArea();
        messageArea.setWrapText(true);
        messageArea.setPrefHeight(150);
        layout.getChildren().add(messageArea);
        
        // Submit button for creating the answer
        Button submitButton = new Button("Send Message");
        submitButton.setOnAction(e -> {
            String messageText = messageArea.getText().trim();
            if (!messageText.isEmpty()) {
            	String result = "";
				if (review != null) {
					result = databaseHelper.sendMessage(messageID, userName, review.getUserName(), review.getReviewID(), messageText);
	        		if (question != null) new ReviewViewPage(databaseHelper, review, question).show(stage, userName);
	        		else new ReviewViewPage(databaseHelper, review, answer).show(stage, userName);
	        	}
	        	else {
	        		result = databaseHelper.sendMessage(messageID, userName, question.getUserName(), question.getQuestionID(), messageText);
	        		new AnswerListPage(databaseHelper, question).show(stage, userName);
	        	}
				System.out.println(result);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Message cannot be empty!", ButtonType.OK);
                alert.showAndWait();
            }
        });
        layout.getChildren().add(submitButton);
        
        // Back button to return without creating an answer
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
        	if (review != null) {
        		if (question != null) new ReviewViewPage(databaseHelper, review, question).show(stage, userName);
        		else new ReviewViewPage(databaseHelper, review, answer).show(stage, userName);
        	}
        	else new AnswerListPage(databaseHelper, question).show(stage, userName);
        });
        layout.getChildren().add(backButton);
        
        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Create Answer for Question " + question.getContent());
        stage.show();
    }
}
