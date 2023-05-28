package WIA1002;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class addFriendsController {
    @FXML
    private AnchorPane contentPane;

    @FXML
    private void suggestButtonOnAction() {
        // Implement the logic for suggesting friends to add
        // This method will be called when the "Suggest Friends to Add" button is clicked
    }

    @FXML
    private void showRequestButtonOnAction() {
        try {
            // Load the friend request FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("friendrequest.fxml"));
            Pane friendRequestPane = loader.load();

            // Set the friend request pane as the content of the contentPane
            contentPane.getChildren().setAll(friendRequestPane);
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }
}
