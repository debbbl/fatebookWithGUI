package WIA1002;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("suggestFriendsToAdd.fxml"));
            Pane suggestMutualFriendPane = loader.load();

            // Set up the controller for the suggestFriendsToAdd.fxml if needed
            suggestFriendsToAddController suggestController = loader.getController();
            suggestController.setUser(user);

            // Set the suggestMutualFriendPane as the content of the contentPane
            contentPane.getChildren().setAll(suggestMutualFriendPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showRequestButtonOnAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("friendRequest.fxml"));
            Pane friendRequestPane = loader.load();

            FriendRequestController friendRequestController = loader.getController();
            friendRequestController.setUser(user);

            // Set the friendRequestPane as the content of the contentPane
            contentPane.getChildren().setAll(friendRequestPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
