package WIA1002;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class friendRequestController implements Initializable {
    @FXML
    private ListView<String> friendRequestListView;
    @FXML
    private ImageView profileImageView;
    @FXML
    private Label usernameLabel;
    @FXML
    private Button acceptButton;
    @FXML
    private Button rejectButton;

    private tempDatabase database;
    private int userId;
    private String username;
    private ObservableList<String> friendRequests;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = new tempDatabase();
        friendRequests = FXCollections.observableArrayList();

        friendRequestListView.setItems(friendRequests);
        friendRequestListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showFriendRequestDetails(newValue);
            }
        });
    }

    public void setUserDetails(int userId, String username) {
        this.userId = userId;
        this.username = username;
        loadFriendRequests();
    }

    private void loadFriendRequests() {
        Connection connection = database.getConnection();
        try {
            String query = "SELECT * FROM friendrequest WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int friendRequestId = resultSet.getInt("request_id");
                int friendId = resultSet.getInt("friend_id");
                String friendUsername = resultSet.getString("friend_username");
                boolean accepted = resultSet.getBoolean("accepted_request");

                String friendRequest = "ID: " + friendRequestId + ", Username: " + friendUsername + ", Accepted: " + accepted;
                friendRequests.add(friendRequest);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void showFriendRequestDetails(String friendRequest) {
        String[] parts = friendRequest.split(",");
        String username = parts[1].substring(parts[1].indexOf(":") + 2);
        profileImageView.setImage(getProfileImage(username));
        usernameLabel.setText(username);
    }

    @FXML
    private void acceptButtonClicked() {
        String selectedFriendRequest = friendRequestListView.getSelectionModel().getSelectedItem();
        if (selectedFriendRequest != null) {
            int friendRequestId = Integer.parseInt(selectedFriendRequest.substring(selectedFriendRequest.indexOf(":") + 2, selectedFriendRequest.indexOf(",")));
            updateFriendRequest(friendRequestId, true);
            friendRequests.remove(selectedFriendRequest);

            showAlert(Alert.AlertType.INFORMATION, "Friend Request Accepted", "You have accepted the friend request.");
        }
    }

    @FXML
    private void rejectButtonClicked() {
        String selectedFriendRequest = friendRequestListView.getSelectionModel().getSelectedItem();
        if (selectedFriendRequest != null) {
            int friendRequestId = Integer.parseInt(selectedFriendRequest.substring(selectedFriendRequest.indexOf(":") + 2, selectedFriendRequest.indexOf(",")));
            updateFriendRequest(friendRequestId, false);
            friendRequests.remove(selectedFriendRequest);

            showAlert(Alert.AlertType.INFORMATION, "Friend Request Rejected", "You have rejected the friend request.");
        }
    }

    private Image getProfileImage(String username) {
        byte[] profilePicData = database.getProfilePicture(username);
        if (profilePicData != null) {
            return new Image(new ByteArrayInputStream(profilePicData));
        } else {
            // Set a default profile image if the user doesn't have one
            return new Image(getClass().getResourceAsStream("default_profile_image.png"));
        }
    }

    private void updateFriendRequest(int friendRequestId, boolean accept) {
        Connection connection = database.getConnection();
        try {
            String updateQuery = "UPDATE friendrequest SET accepted_request = ? WHERE request_id = ?";
            PreparedStatement statement = connection.prepareStatement(updateQuery);
            statement.setBoolean(1, accept);
            statement.setInt(2, friendRequestId);
            statement.executeUpdate();
            System.out.println("Friend request updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
