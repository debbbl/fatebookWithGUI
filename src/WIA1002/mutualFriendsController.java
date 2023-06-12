package WIA1002;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class mutualFriendsController {

    @FXML
    private Button findMutualFriendsButton;

    @FXML
    private ListView<String> mutualFriendsListView;

    @FXML
    private Label resultLabel;

    @FXML
    private Button cancelButton;

    private regularUser user;

    private String selectedUserName;

    private MutualFriendsGraph mutualFriendsGraph;
    private Database database = new Database();

    public mutualFriendsController() {
        mutualFriendsGraph = new MutualFriendsGraph();
    }

    public void setUser(regularUser user,String selectedUserName) {
        this.user = user;
        this.selectedUserName = selectedUserName;
        findMutualFriendsOnAction();
    }

    @FXML
    private void initialize() {
    }

    @FXML
    private void findMutualFriendsOnAction() {
        List<String> mutualFriends = database.findMutualFriends(user, selectedUserName);

        if (mutualFriends.isEmpty()) {
            displayAlert(Alert.AlertType.INFORMATION, "No Results", "No mutual friends found between " + user.getUsername() + " and " + selectedUserName);
        } else {
            // Update UI components
            mutualFriendsListView.getItems().clear();
            mutualFriendsListView.getItems().addAll(mutualFriends);
            resultLabel.setText("Found " + mutualFriends.size() + " mutual friends.");
        }
    }

    private void displayAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void cancelButtonOnAction() {
        // Close the show mutual friend screen
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
