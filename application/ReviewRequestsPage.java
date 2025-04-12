package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ReviewRequestsPage {

    private final DatabaseHelper databaseHelper;

    public ReviewRequestsPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, String instructorName) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label title = new Label("Pending Reviewer Role Requests");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        layout.getChildren().add(title);

        List<String> requests = databaseHelper.getAllPendingReviewerRequests();

        if (requests.isEmpty()) {
            layout.getChildren().add(new Label("No reviewer requests found."));
        } else {
            for (String username : requests) {
                HBox row = new HBox(10);
                row.setStyle("-fx-alignment: center;");
                Label nameLabel = new Label(username);
                Button approveBtn = new Button("Approve");

                approveBtn.setOnAction(e -> {
                    databaseHelper.approveReviewerRole(username);
                    show(primaryStage, instructorName); // Refresh the page
                });

                row.getChildren().addAll(nameLabel, approveBtn);
                layout.getChildren().add(row);
            }
        }

        Button viewUsersButton = new Button("View All Users");
        viewUsersButton.setOnAction(a -> {
            new InstructorUserListPage(databaseHelper).show(primaryStage, instructorName);
        });
        layout.getChildren().add(viewUsersButton);
        
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new InstructorHomePage(databaseHelper).show(primaryStage, instructorName));
        layout.getChildren().add(backButton);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Review Role Requests");
    }
}