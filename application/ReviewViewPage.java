package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ReviewViewPage {

	private final DatabaseHelper databaseHelper;
    private final Review review;
    private Question question = null;
    private Answer answer = null;
    
    public ReviewViewPage(DatabaseHelper databaseHelper, Review r, Question q) {
    	this.databaseHelper = databaseHelper;
    	this.review = r;
    	this.question = q;
    }
    
    public ReviewViewPage(DatabaseHelper databaseHelper, Review r, Answer a) {
    	this.databaseHelper = databaseHelper;
    	this.review = r;
    	this.answer = a;
    }
    
    // Displays the Review for a question when passed the userName
    public void show(Stage stage, String userName) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        Label headerLabel = new Label("Review: " + review.getReviewText());
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        layout.getChildren().add(headerLabel);
        
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
        	if (question == null) new AnswerViewPage(databaseHelper, answer).show(stage, userName);
        	else new AnswerListPage(databaseHelper, question).show(stage, userName);
        });
        layout.getChildren().add(backButton);
        
        if (review.getUserName().equals(userName)) {
        	Button updateReviewButton = new Button("Edit Review");
        	updateReviewButton.setOnAction(e -> {
        		if (question != null) new CreateReviewPage(databaseHelper, question, review.getReviewID()).show(stage, userName);
        		else new CreateReviewPage(databaseHelper, answer, review.getReviewID()).show(stage, userName);
            });
            layout.getChildren().add(updateReviewButton);
            
            Button deleteReviewButton = new Button("Delete Review");
    		deleteReviewButton.setOnAction(a-> {
    			Alert deleteConfirmation = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete your review?");
        		deleteConfirmation.showAndWait().ifPresent(response -> {
        		     if (response == ButtonType.OK) {
        		    	 databaseHelper.deleteReview(review.getReviewID());
        		    	 if (question != null) new AnswerListPage(databaseHelper, question).show(stage, userName);
        		    	 else new AnswerViewPage(databaseHelper, answer).show(stage, userName);
        		     }
        		});
        		
    		});	
    		layout.getChildren().add(deleteReviewButton);
    		
        }
        else {
        	Button privateMessageButton = new Button("Send Private Message");
            privateMessageButton.setOnAction(e -> {
            	if (question == null) new SendMessagePage(databaseHelper, review, answer).show(stage, userName);
            	else new SendMessagePage(databaseHelper, review, question).show(stage, userName);
            });
            layout.getChildren().add(privateMessageButton);
        }
        
        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.setTitle("");
        stage.show();
    }
}