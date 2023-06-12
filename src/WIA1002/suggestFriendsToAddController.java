package WIA1002;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class suggestFriendsToAddController implements Initializable {
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
    private ListView<regularUser> friendListView;

    @FXML
    private Button viewProfileButton;

    @FXML
    private Button sendFriendRequestButton;

    private ObservableList<regularUser> suggestedFriends;

    private regularUser user;
    private final Database database = new Database();

    public void setUser(regularUser user){
        this.user = user;
        loadFriendSuggestions();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        suggestedFriends = FXCollections.observableArrayList();

        // Set the suggested friends as the data source for the ListView
        friendListView.setItems(suggestedFriends);

        // Set the cell factory to display the username
        friendListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<regularUser> call(ListView<regularUser> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(regularUser friend, boolean empty) {
                        super.updateItem(friend, empty);
                        if (friend != null) {
                            // Set the username and number of mutual friends
                            setText(friend.getUsername() + " (" + friend.getMutualConnectionsCount() + " mutual friends)");
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });

        // Set the event handler for selecting a friend
        friendListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Enable the buttons when a friend is selected
                viewProfileButton.setDisable(false);
                sendFriendRequestButton.setDisable(false);
            } else {
                // Disable the buttons when no friend is selected
                viewProfileButton.setDisable(true);
                sendFriendRequestButton.setDisable(true);
            }
        });
    }

    public void loadFriendSuggestions() {
        suggestedFriends.clear();

        // Get suggested friend list
        List<regularUser> suggestedFriendsList = database.getSuggestedFriendList(user.getUsername());

        // Filter out duplicates and existing friends
        Set<regularUser> filteredSuggestions = new HashSet<>();
        for (regularUser friend : suggestedFriendsList) {
            if (!friend.getUsername().equals(user.getUsername()) &&
                    !database.getUserFriendList1(user.getUsername()).contains(friend)) {
                filteredSuggestions.add(friend);
            }
        }

        // Calculate and set the mutual connections count for each friend
        for (regularUser friend : filteredSuggestions) {
            int mutualConnectionsCount = database.getMutualConnectionsCount(user.getUsername(), friend.getUsername());
            friend.setMutualConnectionsCount(mutualConnectionsCount);
        }

        // Add the filtered suggestions to the suggestion list
        suggestedFriends.addAll(filteredSuggestions);

        // Sort suggested friends based on mutual connections count
        suggestedFriends.sort(Comparator.comparingInt(regularUser::getMutualConnectionsCount).reversed());
    }
    @FXML
    private void viewProfileButtonClicked() {
        regularUser selectedUsername = friendListView.getSelectionModel().getSelectedItem();

        if (selectedUsername != null) {
            regularUser user = database.getUserDetails(selectedUsername.getUsername());
            displayUserDetails(user);
            user.addActionToHistory("viewed "+selectedUsername+"'s profile",LocalDateTime.now());
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
                // Retrieve profile picture from the database
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
        regularUser selectedUsername = friendListView.getSelectionModel().getSelectedItem();
        try {
            // Load the FXML file for the Edit Account screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mutualFriends.fxml"));
            Parent root = loader.load();

            // Get the controller instance
            mutualFriendsController showMutualFriends  = loader.getController();

            // Pass the user object and the selected user to the mutual friends controller
            showMutualFriends.setUser(user,selectedUsername.getUsername());

            if (showMutualFriends.findMutualFriendsOnAction()) {
                // Create a new Stage for the mutualFriends screen
                Stage mutualFriendsStage = new Stage();
                mutualFriendsStage.initStyle(StageStyle.UNDECORATED);
                mutualFriendsStage.setScene(new Scene(root));

                // Show the mutualFriends screen
                mutualFriendsStage.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Handle error loading the Edit Account screen
        }
    }

    @FXML
    private void sendFriendRequestButtonClicked() throws SQLException {
        regularUser selectedUsername = friendListView.getSelectionModel().getSelectedItem();
        if (selectedUsername != null) {
            boolean requestSent = database.sendFriendRequest(user.getUserId(), user.getUsername(),selectedUsername.getUsername());

            if (requestSent) {
                displayAlert(Alert.AlertType.INFORMATION, "Friend Request Sent", "Friend request sent to " + selectedUsername.getUsername());
                user.addActionToHistory("sent friend request to "+selectedUsername, LocalDateTime.now());
            } else {
                displayAlert(Alert.AlertType.WARNING, "Request Failed", "Friend request to " + selectedUsername.getUsername() + " already sent or the user is already your friend.");
            }
        }
    }

    private void displayAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
