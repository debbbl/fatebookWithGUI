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
            user.addActionToHistory("viewed " + selectedUsername + "'s profile", LocalDateTime.now());
        }
    }


    @FXML
    private void sendFriendRequestButtonClicked() throws SQLException {
        String selectedUsername = searchResultsListView.getSelectionModel().getSelectedItem();
        if (selectedUsername != null) {
            boolean requestSent = database.sendFriendRequest(user.getUserId(), user.getUsername(),selectedUsername);

            if (requestSent) {
                displayAlert(Alert.AlertType.INFORMATION, "Friend Request Sent", "Friend request sent to " + selectedUsername);
                user.addActionToHistory("sent friend requets to "+selectedUsername,LocalDateTime.now());
            } else {
                displayAlert(Alert.AlertType.WARNING, "Request Failed", "Friend request to " + selectedUsername + " already sent or the user is already your friend.");
            }
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
            // db.updateJob(user.getUsername(), latestJobExperience);

            if (user.getProfilePic() != null) {
                profilePictureImageView.setImage(new Image(new ByteArrayInputStream(user.getProfilePic())));
            } else {
                // Retrieve profile picture from the database
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
            // Load the FXML file for the Edit Account screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mutualFriends.fxml"));
            Parent root = loader.load();

            // Get the controller instance
            mutualFriendsController showMutualFriends  = loader.getController();

            // Pass the user object and the selected user to the mutual friends controller
            showMutualFriends .setUser(user,selectedUsername);

            // Create a new Stage for the Edit Account screen
            Stage editAccountStage = new Stage();
            editAccountStage.initStyle(StageStyle.UNDECORATED);
            editAccountStage.setScene(new Scene(root));

            // Show the Edit Account screen
            editAccountStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            // Handle error loading the Edit Account screen
        }
    }
}
