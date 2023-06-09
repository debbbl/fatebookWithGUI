package WIA1002;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class tempFriendRequestController implements Initializable {
    @FXML
    private ListView<regularUser> friendRequestListView;

    @FXML
    private Button acceptButton;

    @FXML
    private Button rejectButton;

    private regularUser user;
    private tempDatabase database;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        friendRequestListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<regularUser> call(ListView<regularUser> listView) {
                return new FriendRequestCell();
            }
        });
    }

    public void setUser(regularUser user) {
        this.user = user;
        this.database = new tempDatabase();

        // Load friend requests when the controller is initialized
        loadFriendRequests();
    }

    private void loadFriendRequests() {
        // Retrieve friend requests for the logged-in user
        int userId = database.getUserIdByUsername(user.getUsername());
        List<String> friendRequests = database.getFriendRequestsReceived(userId);

        // Create a list of regularUser objects from the usernames
        List<regularUser> friendRequestUsers = new ArrayList<>();
        for (String username : friendRequests) {
            regularUser friendRequestUser = new regularUser.RegularUserBuilder()
                    .username(username) // Set the username
                    .build(); // Build the regularUser object
            friendRequestUsers.add(friendRequestUser);
        }

        // Display friend requests in the ListView
        friendRequestListView.getItems().addAll(friendRequestUsers);
    }

    @FXML
    private void acceptButtonOnAction() {
        regularUser selectedRequest = friendRequestListView.getSelectionModel().getSelectedItem();

        if (selectedRequest != null) {
            // Accept the selected friend request
            int userId = database.getUserIdByUsername(user.getUsername());
            // Remove the accepted request from the ListView
            friendRequestListView.getItems().remove(selectedRequest);
            // Update the num_of_fren field for both sender and receiver
            int receiverId = database.getUserIdByUsername(selectedRequest.getUsername());
            database.acceptFriendRequest(userId,receiverId);

            // Show success message
            showInfoAlert("Friend Request Accepted", "You have accepted the friend request from " + selectedRequest.getUsername());
        } else {
            // No friend request selected
            showErrorAlert("Error", "Please select a friend request to accept.");
        }
    }

    @FXML
    private void rejectButtonOnAction() {
        regularUser selectedRequest = friendRequestListView.getSelectionModel().getSelectedItem();

        if (selectedRequest != null) {
            // Reject the selected friend request
            int userId = database.getUserIdByUsername(user.getUsername());
            database.rejectFriendRequest(userId, selectedRequest.getUsername());

            // Remove the rejected request from the ListView
            friendRequestListView.getItems().remove(selectedRequest);

            // Show success message
            showInfoAlert("Friend Request Rejected", "You have rejected the friend request from " + selectedRequest.getUsername());
        } else {
            // No friend request selected
            showErrorAlert("Error", "Please select a friend request to reject.");
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private class FriendRequestCell extends ListCell<regularUser> {
        @Override
        protected void updateItem(regularUser friendRequest, boolean empty) {
            super.updateItem(friendRequest, empty);

            if (empty || friendRequest == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox hbox = new HBox();
                hbox.setSpacing(10);

                // Profile picture
                ImageView profilePic = new ImageView();
                profilePic.setFitWidth(50);
                profilePic.setFitHeight(50);
                byte[] profilePicData = database.getProfilePicture(friendRequest.getUsername());
                if (profilePicData != null) {
                    Image image = new Image(new ByteArrayInputStream(profilePicData));
                    profilePic.setImage(image);
                }
                hbox.getChildren().add(profilePic);

                // Name and username
                VBox vbox = new VBox();
                Text nameText = new Text(friendRequest.getName());
                Text usernameText = new Text(friendRequest.getUsername());
                vbox.getChildren().addAll(nameText, usernameText);
                hbox.getChildren().add(vbox);

                setGraphic(hbox);
            }
        }
    }
}


