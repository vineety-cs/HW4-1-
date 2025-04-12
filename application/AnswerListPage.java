package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;

public class AnswerListPage {
    private final DatabaseHelper databaseHelper;
    private final Question question;
    
    // Answer list page must be created using the question id
    public AnswerListPage(DatabaseHelper databaseHelper, Question q) {
        this.databaseHelper = databaseHelper;
        this.question = q;
    }
    
    // Displays the answers for a question when passed the userName
    public void show(Stage stage, String userName) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        //Label headerLabel = new Label("Answers for Question: " + questionID);
        Label headerLabel = new Label("Question: " + question.getContent());
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        layout.getChildren().add(headerLabel);
        
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
        	new QuestionListPage(databaseHelper).show(stage, userName);
        });
        layout.getChildren().add(backButton);
        
        Button createAnswerButton = new Button("Create Answer");
        createAnswerButton.setOnAction(e -> {
            new CreateAnswerPage(databaseHelper, question).show(stage, userName);
        });
        layout.getChildren().add(createAnswerButton);
        
        // Create a Review Button if user is a Reviewer
        String role = databaseHelper.getUserRoleList(userName);
        //System.out.println("Role: " + role);
        if(role.contains("Reviewer")) {
        	Button createReviewButton = new Button("Create Review of Question");
        	createReviewButton.setOnAction(e -> {
        		new CreateReviewPage(databaseHelper, question).show(stage, userName);
        	});
        	layout.getChildren().add(createReviewButton);
        	
        }
        
        
        
        ArrayList<Answer> answers = databaseHelper.getAnswersForQuestion(question);
        ArrayList<Review> reviews = databaseHelper.getReviewsForQuestion(question);
        
        if (question.getUserName().equals(userName)) {
        	Button updateQuestionButton = new Button("Edit Question");
        	updateQuestionButton.setOnAction(e -> {
                new QuestionPage(databaseHelper, question.getQuestionID()).show(stage, userName);
            });
            layout.getChildren().add(updateQuestionButton);
            
            Button deleteQuestionButton = new Button("Delete Question");
    		deleteQuestionButton.setOnAction(a-> {
    			Alert deleteConfirmation = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete your question?");
        		deleteConfirmation.showAndWait().ifPresent(response -> {
        		     if (response == ButtonType.OK) {
        		    	 databaseHelper.deleteQuestion(question.getQuestionID());
        		    	 new QuestionListPage(databaseHelper).show(stage, userName);
        		     }
        		});
    		});	
    		layout.getChildren().add(deleteQuestionButton);
    		
    		Button unresolveQuestionButton = new Button("Unresolve Question");
    		unresolveQuestionButton.setOnAction(a-> {
    			for (Answer ans : answers) {
    				databaseHelper.setResolution(ans.getAnswerID(), false);
    			}
    			new AnswerListPage(databaseHelper, question).show(stage, userName);
    		});	
    		for (Answer ans : answers) 
    		{
    			if (ans.getResolution()) {
    				layout.getChildren().add(unresolveQuestionButton);
    				break;
    			}
    		}
        }
        else {
        	Button privateMessageButton = new Button("Send Private Message");
            privateMessageButton.setOnAction(e -> {
            	new SendMessagePage(databaseHelper, question).show(stage, userName);
            });
            layout.getChildren().add(privateMessageButton);
        }
        
        Label answersLabel = new Label("Student Answers");
        answersLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
        layout.getChildren().add(answersLabel);
		ListView<Answer> answerListView = new ListView<>();
        answerListView.setStyle("-fx-font-size: 14px;");
        answerListView.getItems().setAll(answers);
        answerListView.setOnMouseClicked(event -> {
            Answer selectedAnswer = answerListView.getSelectionModel().getSelectedItem();
            if (event.getClickCount() == 2) {
  				new AnswerViewPage(databaseHelper, selectedAnswer).show(stage, userName);
            }
        });
        layout.getChildren().add(answerListView);
        
        Label reviewsLabel = new Label("Question Reviews");
        reviewsLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
        layout.getChildren().add(reviewsLabel);
		ListView<Review> reviewListView = new ListView<>();
		reviewListView.setStyle("-fx-font-size: 14px;");
		reviewListView.getItems().setAll(reviews);
		reviewListView.setOnMouseClicked(event -> {
            Review selectedReview = reviewListView.getSelectionModel().getSelectedItem();
            if (event.getClickCount() == 2) {
  				new ReviewViewPage(databaseHelper, selectedReview, question).show(stage, userName);
            }
        });
        layout.getChildren().add(reviewListView);
        
        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Answers for Question " + question.getContent());
        stage.show();
    }
    
    @SuppressWarnings("unused")
	private VBox createAnswerDisplay(Answer answer) {
        VBox answerBox = new VBox(5);
        answerBox.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-padding: 5; -fx-background-color: #f9f9f9;");
        
        Label userLabel = new Label("User: " + answer.getUserName());
        userLabel.setStyle("-fx-font-weight: bold;");
        
        TextArea answerTextArea = new TextArea(answer.getAnswerText());
        answerTextArea.setEditable(false);
        answerTextArea.setWrapText(true);
        answerTextArea.setPrefHeight(100);
        
        answerBox.getChildren().addAll(userLabel, answerTextArea);
        return answerBox;
    }
}