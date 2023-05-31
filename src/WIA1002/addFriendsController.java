package WIA1002;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class addFriendsController {
    @FXML
    private AnchorPane contentPane;

    private regularUser user;

    public void setUser(regularUser user) {
        this.user = user;
    }

    @FXML
    private void suggestButtonOnAction() {
        // Implement the logic for suggesting friends to add
        // This method will be called when the "Suggest Friends to Add" button is clicked
    }

    @FXML
    private void showRequestButtonOnAction() {
        try {
            // Load the friend request FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("friendRequest.fxml"));
//            Pane friendRequestPane = loader.load();
            Parent friendRequest = loader.load();

            friendRequestController friendRequestController = loader.getController();
            friendRequestController.setUser(user);

            Stage friendRequestStage = new Stage();
            friendRequestStage.initStyle(StageStyle.UNDECORATED);
            friendRequestStage.setScene(new Scene(friendRequest));

            // Show the friend request screen
            friendRequestStage.showAndWait();

//            // Set the friend request pane as the content of the contentPane
//            contentPane.getChildren().setAll(friendRequestPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
