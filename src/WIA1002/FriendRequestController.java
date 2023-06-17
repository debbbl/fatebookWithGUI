package WIA1002;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.ByteArrayInputStream;
import java.net.URL;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FriendRequestController implements Initializable {
    @FXML
    private ListView<regularUser> friendRequestListView;
    @FXML
    private ImageView profilePictureImageView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label contactNumberLabel;
    @FXML
    private Label genderLabel;
    @FXML
    private Label jobLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Label birthdayLabel;
    @FXML
    private Label hobbiesLabel;
    @FXML
    private Label relationshipStatusLabel;
    @FXML
    private Label addressLabel;

    @FXML
    private Button acceptButton;

    @FXML
    private Button rejectButton;

    private regularUser user;
    private Database database = new Database();

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
        loadFriendRequests();
    }

    private void loadFriendRequests() {
        int userId = database.getUserIdByUsername(user.getUsername());
        List<String> friendRequests = database.getFriendRequestsReceived(userId);

        List<regularUser> friendRequestUsers = new ArrayList<>();
        for (String username : friendRequests) {
            regularUser friendRequestUser = new regularUser.RegularUserBuilder()
                    .username(username)
                    .build();
            friendRequestUsers.add(friendRequestUser);
        }

        friendRequestListView.getItems().addAll(friendRequestUsers);
    }

    @FXML
    private void acceptButtonOnAction() {
        regularUser selectedRequest = friendRequestListView.getSelectionModel().getSelectedItem();

        if (selectedRequest != null) {
            int userId = database.getUserIdByUsername(user.getUsername());
            friendRequestListView.getItems().remove(selectedRequest);
            int receiverId = database.getUserIdByUsername(selectedRequest.getUsername());
            database.acceptFriendRequest(userId,receiverId);
            user.addActionToHistory("accepted "+selectedRequest.getUsername()+"'s friend request",LocalDateTime.now());

            showInfoAlert("Friend Request Accepted", "You have accepted the friend request from " + selectedRequest.getUsername());
        } else {
            showErrorAlert("Error", "Please select a friend request to accept.");
        }
    }

    @FXML
    private void rejectButtonOnAction() {
        regularUser selectedRequest = friendRequestListView.getSelectionModel().getSelectedItem();

        if (selectedRequest != null) {
            int userId = database.getUserIdByUsername(user.getUsername());
            database.rejectFriendRequest(userId, selectedRequest.getUsername());

            friendRequestListView.getItems().remove(selectedRequest);
            user.addActionToHistory("rejected "+selectedRequest.getUsername()+"'s friend request",LocalDateTime.now());

            showInfoAlert("Friend Request Rejected", "You have rejected the friend request from " + selectedRequest.getUsername());
        } else {
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
    @FXML
    private void viewProfileButtonClicked() {
        regularUser selectedUsername = friendRequestListView.getSelectionModel().getSelectedItem();

        if (selectedUsername != null) {
            regularUser user = database.getUserDetails(selectedUsername.getUsername());
            displayUserDetails(user);
            user.addActionToHistory("viewed "+selectedUsername+"'s profile", LocalDateTime.now());
        }
    }

    private int calculateAge(LocalDate birthday) {
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthday, currentDate).getYears();
    }
    private void displayUserDetails(regularUser user) {
        if (user != null) {
            nameLabel.setText(user.getName() != null ? user.getName() : "N/A");
            emailLabel.setText(user.getEmail() != null ? user.getEmail() : "N/A");
            usernameLabel.setText(user.getUsername() != null ? user.getUsername() : "N/A");
            contactNumberLabel.setText(user.getContactNumber() != null ? user.getContactNumber() : "N/A");
            LocalDate birthday = user.getBirthday();
            birthdayLabel.setText(birthday != null ? birthday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A");
            ageLabel.setText(birthday != null ? Integer.toString(calculateAge(birthday)) : "N/A");
            genderLabel.setText(user.getGender());
            String jobExperiences = user.getCurrentJobExperience();
            String latestJobExperience = "";
            if (jobExperiences != null && !jobExperiences.isEmpty()) {
                String[] experiences = jobExperiences.split(",");
                if (experiences.length > 0) {
                    latestJobExperience = experiences[experiences.length - 1].trim();
                }
            }
            jobLabel.setText(!latestJobExperience.isEmpty() ? latestJobExperience : "N/A");
            hobbiesLabel.setText(user.getHobbies() != null ? String.join(", ", user.getHobbies()) : "N/A");
            addressLabel.setText(user.getAddress() != null ? user.getAddress() : "N/A");
            relationshipStatusLabel.setText(user.getRelationshipStatus() != null ? user.getRelationshipStatus() : "N/A");

            if (user.getProfilePic() != null) {
                profilePictureImageView.setImage(new Image(new ByteArrayInputStream(user.getProfilePic())));
            } else {
                byte[] profilePicData = database.getProfilePicture(user.getUsername());
                if (profilePicData != null) {
                    user.setProfilePic(profilePicData);
                    profilePictureImageView.setImage(new Image(new ByteArrayInputStream(profilePicData)));
                }
            }
        }
    }
    @FXML
    private void showMutualFriendsButtonClicked(){
        regularUser selectedUsername = friendRequestListView.getSelectionModel().getSelectedItem();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mutualFriends.fxml"));
            Parent root = loader.load();

            mutualFriendsController showMutualFriends  = loader.getController();

            showMutualFriends.setUser(user,selectedUsername.getUsername());

            if (showMutualFriends.findMutualFriendsOnAction()) {
                Stage editAccountStage = new Stage();
                editAccountStage.initStyle(StageStyle.UNDECORATED);
                editAccountStage.setScene(new Scene(root));

                editAccountStage.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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

                ImageView profilePic = new ImageView();
                profilePic.setFitWidth(50);
                profilePic.setFitHeight(50);
                byte[] profilePicData = database.getProfilePicture(friendRequest.getUsername());
                if (profilePicData != null) {
                    Image image = new Image(new ByteArrayInputStream(profilePicData));
                    profilePic.setImage(image);
                }
                hbox.getChildren().add(profilePic);

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


