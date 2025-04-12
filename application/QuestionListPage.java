package application;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import databasePart1.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class QuestionListPage {
	
	private final DatabaseHelper databaseHelper;
	
	public QuestionListPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}
	
	public void show(Stage primaryStage, String userName) {
		VBox layout = new VBox(10);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    Button logoutButton = new Button("Back to Student Page");
	    logoutButton.setOnAction(a -> {
	    	new StudentHomePage(databaseHelper).show(primaryStage, userName);
        });
	    layout.getChildren().add(logoutButton);
	    
	    
	    Button askQuestionButton = new Button("Ask a Question");
	    askQuestionButton.setOnAction(a -> {
	    	new QuestionPage(databaseHelper).show(primaryStage, userName);
	    });
	    layout.getChildren().add(askQuestionButton);
	  	
	    // Label to display Hello user
	    Label userLabel = new Label("Questions Asked");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    layout.getChildren().add(userLabel);
	    Scene userScene = new Scene(layout, 1200, 600);

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Questions Page");
	    
	    /**
	     * Karen - Start of search questions implementation
	     * 
	     * This section allows students to search for specific questions out of those
	     * that have been asked.
	     * 
	     * Searching for answers can be done possibly in each answer page using the same methods.
	     * 
	     * */
	    TextField searchField = new TextField();
        searchField.setPromptText("Search for a question...");
        searchField.setStyle("-fx-font-size: 14px; -fx-padding: 8px;");

        ListView<Question> resultsList = new ListView<>();
        resultsList.setStyle("-fx-font-size: 14px;");

        //Get questions from the database, ends up returning them as a single String
        ArrayList<Question> questions = databaseHelper.getAllQuestions();
        
        // Show all questions initially
        resultsList.getItems().setAll(questions);

        // Filters questions as the user types using filterQuestions method
        searchField.textProperty().addListener((input, oldValue, newValue) -> {
            List<Question> filteredQuestions = filterQuestions(questions, newValue);
            resultsList.getItems().setAll(filteredQuestions);
        });
        
        // When a question is clicked, its corresponding AnswerPage should be displayed
        resultsList.setOnMouseClicked(event -> {
            Question selectedQuestion = resultsList.getSelectionModel().getSelectedItem();
            if (selectedQuestion != null) {
            	
            	//Test to verify the right question associated with click
            	System.out.println(selectedQuestion + " was clicked.");

            }
            if (event.getClickCount() == 2) {
  				new AnswerListPage(databaseHelper, selectedQuestion).show(primaryStage, userName);
            }
        });
	    
        //updateQuestion(databaseHelper, layout, userName);
        layout.getChildren().addAll(searchField, resultsList);
	    
	
	}
	
	
	//Filter search results method
	private List<Question> filterQuestions(List<Question> allQuestions, String keyword) {
		if (keyword.isEmpty()) return allQuestions; // Show all questions if input is empty
		return allQuestions.stream().filter(q -> q.getContent().toLowerCase().contains(keyword.toLowerCase())).collect(Collectors.toList());
	}

}
