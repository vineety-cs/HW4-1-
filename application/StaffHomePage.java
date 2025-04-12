package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page allows users with the Staff role to access moderation functionalities.
 */
public class StaffHomePage {

    private final DatabaseHelper databaseHelper;

    public StaffHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Staff home page.
     * @param primaryStage The primary stage where the scene will be displayed.
     * @param userName The username of the currently logged-in staff member.
     */
    public void show(Stage primaryStage, String userName) {
        VBox layout = new VBox(5);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label userLabel = new Label("Your Staff Page");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button changeInfoButton = new Button("Change Personal Info");
        changeInfoButton.setOnAction(a -> new ChangeInfoPage(databaseHelper).show(primaryStage, userName));

        Button viewQnAButton = new Button("View All Questions & Answers");
        viewQnAButton.setOnAction(a -> new StaffQuestionAnswerViewPage(databaseHelper).show(primaryStage, userName));

        Button viewMessagesButton = new Button("View All Private Messages");
        viewMessagesButton.setOnAction(a -> new StaffMessagesPage(databaseHelper).show(primaryStage, userName));

        Button modNotesButton = new Button("Moderation Notes");
        modNotesButton.setOnAction(a -> new StaffModerationNotesPage(databaseHelper).show(primaryStage, userName));

        Button logoutButton = new Button("Back to Home Page");
        logoutButton.setOnAction(a -> new HomePageSelectionPage(databaseHelper).show(primaryStage, userName));

        layout.getChildren().addAll(userLabel, changeInfoButton, viewQnAButton, viewMessagesButton, modNotesButton, logoutButton);
        Scene userScene = new Scene(layout, 1200, 600);
        primaryStage.setScene(userScene);
        primaryStage.setTitle("Staff Page");
        primaryStage.show();
    }
}
