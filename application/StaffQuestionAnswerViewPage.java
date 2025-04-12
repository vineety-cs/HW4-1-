package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;

/**
 * A JavaFX page that displays all questions in the system for staff review.
 */
public class StaffQuestionAnswerViewPage {
    private final DatabaseHelper databaseHelper;

    public StaffQuestionAnswerViewPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the page listing all questions.
     * @param primaryStage The primary stage for this application.
     * @param userName The staff member's user name.
     */
    public void show(Stage primaryStage, String userName) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // ListView to display questions
        ListView<String> questionList = new ListView<>();
        ArrayList<Question> questions = databaseHelper.getAllQuestions();
        for (Question q : questions) {
            questionList.getItems().add(q.toString());
        }
        layout.getChildren().add(questionList);

        // Flag button: flags the selected question
        Button flagButton = new Button("Flag Selected Question");
        flagButton.setOnAction(e -> {
            int selectedIndex = questionList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                Question selectedQuestion = questions.get(selectedIndex);
                databaseHelper.flagQuestion(selectedQuestion.getQuestionID());
            }
        });
        layout.getChildren().add(flagButton);

        // Back button to StaffHomePage
        Button backButton = new Button("Back to Staff Home");
        backButton.setOnAction(e -> {
            new StaffHomePage(databaseHelper).show(primaryStage, userName);
        });
        layout.getChildren().add(backButton);

        Scene scene = new Scene(layout, 1200, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Staff - All Questions");
        primaryStage.show();
    }
}
