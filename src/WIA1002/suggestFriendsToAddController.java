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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    private tempDatabase db = new tempDatabase();

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
        friendListView.setCellFactory(new Callback<ListView<regularUser>, ListCell<regularUser>>() {
            @Override
            public ListCell<regularUser> call(ListView<regularUser> listView) {
                return new ListCell<regularUser>() {
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
        List<regularUser> suggestedFriendsList = db.getSuggestedFriendList(user.getUsername());

        // Filter out duplicates and existing friends
        Set<regularUser> filteredSuggestions = new HashSet<>();
        for (regularUser friend : suggestedFriendsList) {
            if (!friend.getUsername().equals(user.getUsername()) &&
                    !db.getUserFriendList1(user.getUsername()).contains(friend) &&
                    !filteredSuggestions.contains(friend)) {
                filteredSuggestions.add(friend);
            }
        }

        // Calculate and set the mutual connections count for each friend
        for (regularUser friend : filteredSuggestions) {
            int mutualConnectionsCount = db.getMutualConnectionsCount(user.getUsername(), friend.getUsername());
            friend.setMutualConnectionsCount(mutualConnectionsCount);
        }

        // Add the filtered suggestions to the suggestion list
        suggestedFriends.addAll(filteredSuggestions);

        // Sort suggested friends based on mutual connections count
        suggestedFriends.sort(Comparator.comparingInt(regularUser::getMutualConnectionsCount).reversed());
    }

    public int calculateMutualFriendsCount(regularUser userA, regularUser userB) {
        List<regularUser> userAFriends = db.getUserFriendList1(userA.getUsername());
        List<regularUser> userBFriends = db.getUserFriendList1(userB.getUsername());

        // Find the intersection of userA's and userB's friend lists
        List<regularUser> mutualFriends = userAFriends.stream()
                .filter(userBFriends::contains)
                .collect(Collectors.toList());

        return mutualFriends.size();
    }

    @FXML
    private void viewProfileButtonClicked() {
        regularUser selectedUsername = friendListView.getSelectionModel().getSelectedItem();

        if (selectedUsername != null) {
            regularUser user = getUserDetails(selectedUsername.getUsername());
            displayUserDetails(user);
            user.addActionToHistory("viewed "+selectedUsername+"'s profile",LocalDateTime.now());
        }
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
        regularUser selectedUsername = friendListView.getSelectionModel().getSelectedItem();
        try {
            // Load the FXML file for the Edit Account screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mutualFriends.fxml"));
            Parent root = loader.load();

            // Get the controller instance
            mutualFriendsController showMutualFriends  = loader.getController();

            // Pass the user object and the selected user to the mutual friends controller
            showMutualFriends.setUser(user,selectedUsername.getUsername());

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

    @FXML
    private void sendFriendRequestButtonClicked() throws SQLException {
        regularUser selectedUsername = friendListView.getSelectionModel().getSelectedItem();
        tempDatabase db = new tempDatabase();
        if (selectedUsername != null) {
            boolean requestSent = db.sendFriendRequest(user.getUserId(), user.getUsername(),selectedUsername.getUsername());

            if (requestSent) {
                displayAlert(Alert.AlertType.INFORMATION, "Friend Request Sent", "Friend request sent to " + selectedUsername.getUsername());
                user.addActionToHistory("sent friend requets to "+selectedUsername, LocalDateTime.now());
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
