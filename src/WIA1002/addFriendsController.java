package WIA1002;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.time.LocalDateTime;

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

            suggestFriendsToAddController suggestController = loader.getController();
            suggestController.setUser(user);

            contentPane.getChildren().setAll(suggestMutualFriendPane);
            user.addActionToHistory("Viewed friend suggestions", LocalDateTime.now());
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

            contentPane.getChildren().setAll(friendRequestPane);
            user.addActionToHistory("Viewed friend requests", LocalDateTime.now());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
