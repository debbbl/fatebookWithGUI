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
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class friendRequestController implements Initializable {
    @FXML
    private Button cancelButton;

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

    private regularUser user;

    public void setUser(regularUser user) {
        this.user = user;
        setUserDetails(user.getUserId(),user.getUsername());

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = new tempDatabase();
        friendRequests = FXCollections.observableArrayList();

        friendRequestListView.setItems(friendRequests);
//        friendRequestListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue != null) {
//                showFriendRequestDetails(newValue);
//            }
//        });
    }

    public void setUserDetails(int userId, String username) {
        this.userId = userId;
        this.username = username;
        loadFriendRequests();
    }

    private void loadFriendRequests() {
        Connection connection = database.getConnection();
        try {
            String query = "SELECT * FROM friendrequest WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int friendRequestId = resultSet.getInt("user_id");
                // int friendId = resultSet.getInt("friend_id");
                String friendUsername = resultSet.getString("requestReceived");
                String [] friend = friendUsername.split(";");
                String accepted = resultSet.getString("accepted_request");

                for(String friends : friend)
                {
                    String friendRequest = "ID: " + friendRequestId + ", Username: " + friends + ", Accepted: " + accepted; //the id can be throw as it display the current user id
                    friendRequests.add(friendRequest);
                }

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
            updateFriendRequest(friendRequestId);
            friendRequests.remove(selectedFriendRequest);

            showAlert(Alert.AlertType.INFORMATION, "Friend Request Accepted", "You have accepted the friend request.");
        }
    }

    @FXML
    private void rejectButtonClicked() {
        String selectedFriendRequest = friendRequestListView.getSelectionModel().getSelectedItem();
        if (selectedFriendRequest != null) {
            int friendRequestId = Integer.parseInt(selectedFriendRequest.substring(selectedFriendRequest.indexOf(":") + 2, selectedFriendRequest.indexOf(",")));
            updateFriendRequestReject(friendRequestId);
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

    private void updateFriendRequest(int friendRequestId) {
        Connection connection = database.getConnection();
        String selectedFriendRequest = friendRequestListView.getSelectionModel().getSelectedItem();
        String usernameToRemove = getUsernameFromFriendRequest(selectedFriendRequest);
        try {
            String updateQuery = "UPDATE friendrequest SET accepted_request = CONCAT(IFNULL(accepted_request, ''), ?), requestReceived = REPLACE(requestReceived, ?, '') WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(updateQuery);
            statement.setString(1, usernameToRemove + ";");
            statement.setString(2, usernameToRemove+";");
            statement.setInt(3, friendRequestId);
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

    private void updateFriendRequestReject(int friendRequestId) {
        Connection connection = database.getConnection();
        String selectedFriendRequest = friendRequestListView.getSelectionModel().getSelectedItem();
        String usernameToRemove = getUsernameFromFriendRequest(selectedFriendRequest);
        try {
            String updateQuery = "UPDATE friendrequest SET rejected_request = CONCAT(IFNULL(rejected_request, ''), ?), requestReceived = REPLACE(requestReceived, ?, '') WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(updateQuery);
            statement.setString(1, usernameToRemove + ";");
            statement.setString(2, usernameToRemove+";");
            statement.setInt(3, friendRequestId);
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

    private String getUsernameFromFriendRequest(String friendRequest) {
        String[] parts = friendRequest.split(",");
        String username = parts[1].substring(parts[1].indexOf(":") + 2);
        return username;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void cancelButtonOnAction() {
        // Close the Edit Account screen
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
