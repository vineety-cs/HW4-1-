package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;

public class InboxPage {
    private final DatabaseHelper databaseHelper;
    
    // Answer list page must be created using the question id
    public InboxPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    // Displays the answers for a question when passed the userName
    public void show(Stage stage, String userName) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        Label headerLabel = new Label("Your Messages:");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        layout.getChildren().add(headerLabel);
        
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
        	new StudentHomePage(databaseHelper).show(stage, userName);
        });
        layout.getChildren().add(backButton);
        
        ArrayList<Message> messages = databaseHelper.getInbox(userName);
        
        ListView<Message> resultsList = new ListView<>();
        resultsList.setStyle("-fx-font-size: 14px;");
        resultsList.getItems().setAll(messages);
        
        resultsList.setOnMouseClicked(event -> {
            Message selectedMessage = resultsList.getSelectionModel().getSelectedItem();
            if (selectedMessage != null) {
            	
            	//Test to verify the right question associated with click
            	System.out.println(selectedMessage + " was clicked.");

            }
            if (event.getClickCount() == 2) {
  				new MessageViewPage(databaseHelper, selectedMessage).show(stage, userName);
            }
        });
        layout.getChildren().add(resultsList);
        
        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
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