package application;

import java.util.ArrayList;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AnswerViewPage {
    private final DatabaseHelper databaseHelper;
    private final Answer answer;
    private Question question;
    
    // Answer list page must be created using the question id
    public AnswerViewPage(DatabaseHelper databaseHelper, Answer a) {
        this.databaseHelper = databaseHelper;
        this.answer = a;
    }
    
    // Displays the answers for a question when passed the userName
    public void show(Stage stage, String userName) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        question = databaseHelper.getQuestion(answer.getQuestionID());
        
        Label headerLabel = new Label("Answer: " + answer.getAnswerText());
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        layout.getChildren().add(headerLabel);
        
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
        	new AnswerListPage(databaseHelper, question).show(stage, userName);
        });
        layout.getChildren().add(backButton);
        
        if (answer.getUserName().equals(userName)) {
        	Button updateAnswerButton = new Button("Edit Answer");
        	updateAnswerButton.setOnAction(e -> {
                new CreateAnswerPage(databaseHelper, question, answer.getAnswerID()).show(stage, userName);
            });
            layout.getChildren().add(updateAnswerButton);
            
            Button deleteAnswerButton = new Button("Delete Answer");
    		deleteAnswerButton.setOnAction(a-> {
    			Alert deleteConfirmation = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete your answer?");
        		deleteConfirmation.showAndWait().ifPresent(response -> {
        		     if (response == ButtonType.OK) {
        		    	 databaseHelper.deleteAnswer(answer.getAnswerID());
        		    	 new AnswerListPage(databaseHelper, question).show(stage, userName);
        		     }
        		});
    		});	
    		layout.getChildren().add(deleteAnswerButton);
    		
        }
        if (question.getUserName().equals(userName)) {
        	Button resolveQuestionButton = new Button("Mark Answer as Resolution");
    		resolveQuestionButton.setOnAction(a-> {
    			databaseHelper.setResolution(answer.getAnswerID(), true);
    		});	
    		layout.getChildren().add(resolveQuestionButton);
        }
        
        String role = databaseHelper.getUserRoleList(userName);
        //System.out.println("Role: " + role);
        if(role.contains("Reviewer")) {
        	Button createReviewButton = new Button("Create Review of Answer");
        	createReviewButton.setOnAction(e -> {
        		new CreateReviewPage(databaseHelper, answer).show(stage, userName);
        	});
        	layout.getChildren().add(createReviewButton);
        	
        }
        
        ArrayList<Review> reviews = databaseHelper.getReviewsForAnswer(answer);
        
        Label reviewsLabel = new Label("Answer Reviews");
        reviewsLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
        layout.getChildren().add(reviewsLabel);
		ListView<Review> reviewListView = new ListView<>();
		reviewListView.setStyle("-fx-font-size: 14px;");
		reviewListView.getItems().setAll(reviews);
		reviewListView.setOnMouseClicked(event -> {
            Review selectedReview = reviewListView.getSelectionModel().getSelectedItem();
            if (event.getClickCount() == 2) {
  				new ReviewViewPage(databaseHelper, selectedReview, answer).show(stage, userName);
            }
        });
        layout.getChildren().add(reviewListView);
        
        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Answers for Question " + question.getContent());
        stage.show();
    }
}