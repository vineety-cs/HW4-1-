package application;

import java.util.UUID;
import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.*;


public class QuestionPage {

	String questionID;
	String userName;
	boolean editing = false;
	
	private final DatabaseHelper databaseHelper;

	
	//constructor for a new question
	public QuestionPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
		questionID = UUID.randomUUID().toString();
	}
	
	//constructor for editing a question
	public QuestionPage(DatabaseHelper databaseHelper, String qID) {
		this.databaseHelper = databaseHelper;
		this.questionID = qID;
		this.editing = true;
	}
	
	
	public void show(Stage primaryStage, String userName) {
		Label instructionsLabel = new Label("Enter your " + (editing ? "updated " : "") + "Question");
		
		//Input question
		TextField questionField = new TextField(editing ? databaseHelper.getQuestion(questionID).getContent() : "");
		//questionField.setPromptText("Enter Question");
		questionField.setMaxWidth(1000);
		
		Button backButton = new Button("Back");
		backButton.setOnAction(a -> {
			new QuestionListPage(databaseHelper).show(primaryStage, userName);
		});
		
		
		Button submitQuestionButton = new Button("Submit");
		submitQuestionButton.setOnAction(a-> {
			String questionText = questionField.getText();
			
			if (editing) {
				databaseHelper.updateQuestion(questionID, questionText);
			}
			else {
				databaseHelper.insertNewQuestion(questionID, userName, questionText);
			}
			new QuestionListPage(databaseHelper).show(primaryStage, userName);
		});
		
		
		VBox layout = new VBox(5, instructionsLabel, questionField, backButton, submitQuestionButton);
		layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
		
		primaryStage.setScene(new Scene(layout, 1200, 600));
		primaryStage.setTitle("Ask Your Question");
		primaryStage.show();
	}
	
}

