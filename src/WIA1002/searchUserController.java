package WIA1002;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class searchUserController {
    @FXML
    private TextField searchTextField;
    @FXML
    private ListView<String> searchResultsListView;
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
    private regularUser user;

    private final Database database = new Database();

    public void setUser(regularUser user){
        this.user = user;
    }
    @FXML
    private void searchButtonClicked() {
        String query = searchTextField.getText();
        List<String> searchResults = database.performSearch(query);

        if (searchResults.isEmpty()) {
            displayAlert(Alert.AlertType.INFORMATION, "No Results", "No users found matching the search query.");
        } else {
            searchResultsListView.getItems().clear();
            searchResultsListView.getItems().addAll(searchResults);
            user.addActionToHistory("searched " + query, LocalDateTime.now());
        }
    }
    @FXML
    private void viewProfileButtonClicked() {
        String selectedUsername = searchResultsListView.getSelectionModel().getSelectedItem();

        if (selectedUsername != null) {
            regularUser user = database.getUserDetails(selectedUsername);
            displayUserDetails(user);
            user.addActionToHistory("Viewed " + selectedUsername + "'s profile", LocalDateTime.now());
        }
    }


    @FXML
    private void sendFriendRequestButtonClicked() throws SQLException {
        String selectedUsername = searchResultsListView.getSelectionModel().getSelectedItem();
        if (selectedUsername != null && !selectedUsername.equals(user.getUsername())) {
            System.out.println(user.getUsername());
            boolean requestSent = database.sendFriendRequest(user.getUserId(), user.getUsername(),selectedUsername);

            if (requestSent) {
                displayAlert(Alert.AlertType.INFORMATION, "Friend Request Sent", "Friend request sent to " + selectedUsername);
                user.addActionToHistory("Sent friend request to "+selectedUsername,LocalDateTime.now());
            } else {
                displayAlert(Alert.AlertType.WARNING, "Request Failed", "Friend request to " + selectedUsername + " already sent or the user is already your friend.");
            }
        }else{
            displayAlert(Alert.AlertType.ERROR,"Failed to send","You cannot send friend request to yourself");
        }
    }

    private void displayAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
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

            Database db = new Database();

            if (user.getProfilePic() != null) {
                profilePictureImageView.setImage(new Image(new ByteArrayInputStream(user.getProfilePic())));
            } else {
                byte[] profilePicData = db.getProfilePicture(user.getUsername());
                if (profilePicData != null) {
                    user.setProfilePic(profilePicData);
                    profilePictureImageView.setImage(new Image(new ByteArrayInputStream(profilePicData)));
                }
            }
        }
    }
    @FXML
    private void showMutualFriendsButtonClicked(){
        String selectedUsername = searchResultsListView.getSelectionModel().getSelectedItem();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mutualFriends.fxml"));
            Parent root = loader.load();

            mutualFriendsController showMutualFriends  = loader.getController();
            showMutualFriends .setUser(user,selectedUsername);

            if (showMutualFriends.findMutualFriendsOnAction()) {

                Stage mutualFriendsStage = new Stage();
                mutualFriendsStage.initStyle(StageStyle.UNDECORATED);
                mutualFriendsStage.setScene(new Scene(root));

                mutualFriendsStage.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
