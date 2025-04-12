package application;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class InstructorUserListPage {

    private final DatabaseHelper databaseHelper;

    public InstructorUserListPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, String instructorUserName) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label titleLabel = new Label("User List");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<User> userListView = new ListView<>();

        // Get all users from the database
        ArrayList<User> users = databaseHelper.getAllUsers();
        ObservableList<User> observableUsers = FXCollections.observableArrayList(users);
        userListView.setItems(observableUsers);

        // Display a star if the user has the reviewer role
        userListView.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    String display = user.getUserName();
                    for (String role : user.getRoles()) {
                        if (role != null && role.trim().equalsIgnoreCase("Reviewer")) {
                            display += " \u2605"; 
                            break;
                        }
                    }
                    setText(display);
                }
            }
        });

        // Open SetReviewerPage for a user if double clicked
        userListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                User selectedUser = userListView.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    new SetReviewerPage(databaseHelper)
                        .show(primaryStage, selectedUser.getUserName(), instructorUserName);
                }
            }
        });
        
        // Button to return to the instructors home page
        Button backButton = new Button("Back to Instructor Home");
        backButton.setOnAction(e -> new InstructorHomePage(databaseHelper).show(primaryStage, instructorUserName));

        layout.getChildren().addAll(titleLabel, userListView, backButton);

        Scene scene = new Scene(layout, 1200, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Instructor - All Users");
    }
}