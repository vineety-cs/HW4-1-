package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;

/**
 * A JavaFX page that allows a staff member to add and view moderation notes for a user.
 */
public class StaffModerationNotesPage {
    private final DatabaseHelper databaseHelper;

    public StaffModerationNotesPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the moderation notes page.
     * @param primaryStage The stage on which to display the page.
     * @param userName The staff member's username.
     */
    public void show(Stage primaryStage, String userName) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        // Text field for entering the target user's username
        TextField targetUserField = new TextField();
        targetUserField.setPromptText("Enter username for moderation notes");

        // Text area for entering a new note
        TextArea noteArea = new TextArea();
        noteArea.setPromptText("Enter moderation note here...");

        // Button to save note
        Button saveButton = new Button("Save Moderation Note");
        saveButton.setOnAction(e -> {
            String targetUser = targetUserField.getText();
            String note = noteArea.getText();
            if (!targetUser.isEmpty() && !note.isEmpty()) {
                databaseHelper.insertModerationNote(targetUser, note);
                noteArea.clear();
            }
        });

        // ListView to display existing notes
        ListView<String> notesList = new ListView<>();
        Button viewNotesButton = new Button("View Moderation Notes");
        viewNotesButton.setOnAction(e -> {
            String targetUser = targetUserField.getText();
            if (!targetUser.isEmpty()) {
                ArrayList<String> notes = databaseHelper.getModerationNotes(targetUser);
                notesList.getItems().clear();
                notesList.getItems().addAll(notes);
            }
        });

        // Back button to StaffHomePage
        Button backButton = new Button("Back to Staff Home");
        backButton.setOnAction(e -> {
            new StaffHomePage(databaseHelper).show(primaryStage, userName);
        });

        layout.getChildren().addAll(targetUserField, noteArea, saveButton, viewNotesButton, notesList, backButton);

        Scene scene = new Scene(layout, 1200, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Staff - Moderation Notes");
        primaryStage.show();
    }
}
