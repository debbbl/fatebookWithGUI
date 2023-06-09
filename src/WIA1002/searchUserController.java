package WIA1002;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    @FXML
    private Button addFriendButton;
    private regularUser user;
    @FXML
    private AnchorPane detailsPane;

    @FXML
    private  Button showMutualFriendsButton;

    public void setUser(regularUser user){
        this.user = user;
    }
    @FXML
    private void searchButtonClicked() {
        String query = searchTextField.getText();
        List<String> searchResults = performSearch(query);

        if (searchResults.isEmpty()) {
            displayAlert(Alert.AlertType.INFORMATION, "No Results", "No users found matching the search query.");
        } else {
            searchResultsListView.getItems().clear();
            searchResultsListView.getItems().addAll(searchResults);
            user.addActionToHistory("searched "+query, LocalDateTime.now());
        }
    }
    @FXML
    private void viewProfileButtonClicked() {
        String selectedUsername = searchResultsListView.getSelectionModel().getSelectedItem();

        if (selectedUsername != null) {
            regularUser user = getUserDetails(selectedUsername);
            displayUserDetails(user);
            user.addActionToHistory("viewed "+selectedUsername+"'s profile",LocalDateTime.now());
        }
    }

    @FXML
    private void sendFriendRequestButtonClicked() throws SQLException {
        String selectedUsername = searchResultsListView.getSelectionModel().getSelectedItem();
        tempDatabase db = new tempDatabase();
        if (selectedUsername != null) {
            boolean requestSent = db.sendFriendRequest(user.getUserId(), user.getUsername(),selectedUsername);

            if (requestSent) {
                displayAlert(Alert.AlertType.INFORMATION, "Friend Request Sent", "Friend request sent to " + selectedUsername);
                user.addActionToHistory("sent friend requets to "+selectedUsername,LocalDateTime.now());
            } else {
                displayAlert(Alert.AlertType.WARNING, "Request Failed", "Friend request to " + selectedUsername + " already sent or the user is already your friend.");
            }
        }
    }

    private List<String> performSearch(String query) {
        List<String> searchResults = new ArrayList<>();
        tempDatabase db = new tempDatabase();
        // Perform the database query to retrieve matching usernames
        String searchQuery = "SELECT username FROM userdata WHERE username LIKE ? OR name LIKE ? OR user_id LIKE ? OR email_address LIKE ? OR contact_number LIKE ?";
        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(searchQuery)) {
            String likeQuery = "%" + query + "%";
            statement.setString(1, likeQuery);
            statement.setString(2, likeQuery);
            statement.setString(3, likeQuery);
            statement.setString(4, likeQuery);
            statement.setString(5, likeQuery);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                searchResults.add(resultSet.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return searchResults;
    }
    private void displayAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private regularUser getUserDetails(String username) {
        regularUser user = null;
        tempDatabase db = new tempDatabase();
        // Retrieve the user details from the database based on the username
        String userDetailsQuery = "SELECT * FROM userdata WHERE username = ?";
        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(userDetailsQuery)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                regularUser.RegularUserBuilder userBuilder = new regularUser.RegularUserBuilder();
                userBuilder.username(resultSet.getString("username"))
                        .email(resultSet.getString("email_address"))
                        .contactNumber(resultSet.getString("contact_number"))
                        .name(resultSet.getString("name"))
                        .birthday(resultSet.getObject("birthday", LocalDate.class))
                        .gender(resultSet.getString("gender"))
                        .address(resultSet.getString("address"))
                        .relationshipStatus(resultSet.getString("relationship_status"));


                // Similarly, handle other properties
                String job = resultSet.getString("job");
                Stack<String> jobs = new Stack<>();
                jobs.push(job != null ? job : "N/A");
                userBuilder.jobs(jobs);

                // Handle hobbies (assuming it's a comma-separated string)
                String hobbiesString = resultSet.getString("hobbies");
                List<String> hobbies = (hobbiesString != null && hobbiesString.length() > 0)
                        ? Arrays.asList(hobbiesString.split(","))
                        : Collections.emptyList();
                userBuilder.hobbies(hobbies);


                // Handle profile picture
                byte[] profilePicData = resultSet.getBytes("profile_pic");
                userBuilder.profilePic(profilePicData);

                user = userBuilder.build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
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

            tempDatabase db = new tempDatabase();
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
