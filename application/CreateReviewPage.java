package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.UUID;

public class CreateReviewPage {
	private final DatabaseHelper databaseHelper;
	private final String subjectID;
    private final String reviewID;
    private boolean editing = false;
    private boolean isQuestionReview;
    
    // Create a new Review for a Question
    public CreateReviewPage(DatabaseHelper databaseHelper, Question q) {
    	this.databaseHelper = databaseHelper;
        this.subjectID = q.getQuestionID();
        this.reviewID = UUID.randomUUID().toString().substring(2);
        this.isQuestionReview = true;
    }
    
    // Edit an existing Review of a Question
    public CreateReviewPage(DatabaseHelper databaseHelper, Question q, String rID) {
        this.databaseHelper = databaseHelper;
        this.subjectID = q.getQuestionID();
        this.reviewID = rID;
        this.editing = true;
        this.isQuestionReview = true;
    }
    
    // Create a new Review for an Answer
    public CreateReviewPage(DatabaseHelper databaseHelper, Answer a) {
    	this.databaseHelper = databaseHelper;
        this.subjectID = a.getAnswerID();
        this.reviewID = UUID.randomUUID().toString();
        this.isQuestionReview = false;
    }
    
    // Edit an existing Review of an Answer
    public CreateReviewPage(DatabaseHelper databaseHelper, Answer a, String rID) {
        this.databaseHelper = databaseHelper;
        this.subjectID = a.getAnswerID();
        this.reviewID = rID;
        this.editing = true;
        this.isQuestionReview = false;
    }
    
    // Page is displayed by passing the stage and userName
    public void show(Stage stage, String userName) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        Label header = new Label("Create a New Review of Question");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        layout.getChildren().add(header);
        
        TextArea ReviewArea = new TextArea(editing ? databaseHelper.getReview(reviewID).getReviewText() : "");
        //answerArea.setPromptText("Enter your answer here...");
        ReviewArea.setWrapText(true);
        ReviewArea.setPrefHeight(150);
        layout.getChildren().add(ReviewArea);
        
        // Submit button for creating a Review
        Button submitButton = new Button("Submit Review");
        submitButton.setOnAction(e -> {
            String reviewText = ReviewArea.getText().trim();
            if (!reviewText.isEmpty()) {
            	String result = "";
            	if (editing) databaseHelper.updateReview(reviewID, reviewText);
            	else result = databaseHelper.insertNewReview(reviewID, subjectID, userName, reviewText);
				System.out.println(result);
				if (isQuestionReview) new AnswerListPage(databaseHelper, databaseHelper.getQuestion(subjectID)).show(stage, userName);
	        	else new AnswerViewPage(databaseHelper, databaseHelper.getAnswer(subjectID)).show(stage, userName);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Review cannot be empty!", ButtonType.OK);
                alert.showAndWait();
            }
        });
        layout.getChildren().add(submitButton);
         
        
        // Back button to return without creating a Review
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
        	if (isQuestionReview) 
        	{
        		if (editing) new ReviewViewPage(databaseHelper, databaseHelper.getReview(reviewID), databaseHelper.getQuestion(subjectID)).show(stage, userName);
        		else new AnswerListPage(databaseHelper, databaseHelper.getQuestion(subjectID)).show(stage, userName);
        	}
        	else 
        	{
        		if (editing) new ReviewViewPage(databaseHelper, databaseHelper.getReview(reviewID), databaseHelper.getAnswer(subjectID)).show(stage, userName);
        		else new AnswerViewPage(databaseHelper, databaseHelper.getAnswer(subjectID)).show(stage, userName);
        	}
        });
        layout.getChildren().add(backButton);
        
        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Create Review");
        stage.show();
    }
}