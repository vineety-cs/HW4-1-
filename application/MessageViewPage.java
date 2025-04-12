package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MessageViewPage {
    private final DatabaseHelper databaseHelper;
    private final Message message;
    
    // Answer list page must be created using the question id
    public MessageViewPage(DatabaseHelper databaseHelper, Message m) {
        this.databaseHelper = databaseHelper;
        this.message = m;
    }
    
    // Displays the answers for a question when passed the userName
    public void show(Stage stage, String userName) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label headerLabel = new Label();
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        
        Question question = null;
        Answer answer = null;
        Review review = null;
        
        
        
        review = databaseHelper.getReview(message.getSubjectID());
        if (review == null) {
        	question = databaseHelper.getQuestion(message.getSubjectID());
        	if (question == null) {
        		answer = databaseHelper.getAnswer(message.getSubjectID());
        		question = databaseHelper.getQuestion(answer.getQuestionID());
        		headerLabel.setText(question.getUserName() + "\'s question: " + question.getQuestionText() + "\nYour answer: " + answer.getAnswerText() + "\nMessage: " + message.getMessageText());
        	}
        	else {
        		headerLabel.setText("Your question: " + question.getContent() + "\nMessage: " + message.getMessageText());
        	}
        }
        else {
        	question = databaseHelper.getQuestion(review.getSubjectID());
        	if (question == null) {
        		answer = databaseHelper.getAnswer(review.getSubjectID());
        		question = databaseHelper.getQuestion(answer.getQuestionID());
        		headerLabel.setText(question.getUserName() + "\'s question: " + question.getQuestionText() + "\n" + answer.getUserName() + "\'s answer: " + answer.getAnswerText() + "\nYour review: " + review.getReviewText() + "\nMessage: " + message.getMessageText());
        	}
        	else {
        		headerLabel.setText(question.getUserName() + "\'s question: " + question.getQuestionText() + "\nYour review: " + review.getReviewText() + "\nMessage: " + message.getMessageText());
        	}
        }
        layout.getChildren().add(headerLabel);        
        
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
        	new InboxPage(databaseHelper).show(stage, userName);
        });
        layout.getChildren().add(backButton);
        
        Button deleteMessageButton = new Button("Delete Message");
		deleteMessageButton.setOnAction(a-> {
			Alert deleteConfirmation = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete this message?");
    		deleteConfirmation.showAndWait().ifPresent(response -> {
    		     if (response == ButtonType.OK) {
    		    	 databaseHelper.deleteMessage(message.getMessageID());
    		     }
    		});
    		new InboxPage(databaseHelper).show(stage, userName);
		});	
		layout.getChildren().add(deleteMessageButton);

        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Message from " + message.getSender());
        stage.show();
    }
}