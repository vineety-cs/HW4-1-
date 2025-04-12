package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;

/**
 * A JavaFX page that displays all private messages for staff review.
 */
public class StaffMessagesPage {
    private final DatabaseHelper databaseHelper;

    public StaffMessagesPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays all private messages.
     * @param primaryStage The primary stage to show the page.
     * @param userName The staff member's user name.
     */
    public void show(Stage primaryStage, String userName) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // ListView to display messages
        ListView<String> messageList = new ListView<>();
        ArrayList<Message> messages = databaseHelper.getAllPrivateMessages();
        for (Message m : messages) {
            messageList.getItems().add(m.toString());
        }
        layout.getChildren().add(messageList);

        // Flag button: flags the selected message
        Button flagButton = new Button("Flag Selected Message");
        flagButton.setOnAction(e -> {
            int selectedIndex = messageList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                Message selectedMessage = messages.get(selectedIndex);
                databaseHelper.flagMessage(selectedMessage.getMessageID());
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
        primaryStage.setTitle("Staff - Private Messages");
        primaryStage.show();
    }
}
