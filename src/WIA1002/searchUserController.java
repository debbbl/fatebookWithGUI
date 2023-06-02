package WIA1002;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

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
    private void sendFriendRequestButtonClicked() {
        String selectedUsername = searchResultsListView.getSelectionModel().getSelectedItem();

        if (selectedUsername != null) {
            boolean requestSent = sendFriendRequest(user.getUserId(), user.getUsername(),selectedUsername);

            if (requestSent) {
                displayAlert(Alert.AlertType.INFORMATION, "Friend Request Sent", "Friend request sent to " + selectedUsername);
                user.addActionToHistory("sent friend requets to "+selectedUsername,LocalDateTime.now());
            } else {
                displayAlert(Alert.AlertType.WARNING, "Request Failed", "Friend request to " + selectedUsername + " already sent or the user is already your friend.");
            }
        }
    }

    private boolean sendFriendRequest(int user_id, String username, String sendRequestUsername) {
        tempDatabase db = new tempDatabase();
        Connection connection = db.getConnection();

        try {
            // Check if the friend request has already been sent or if the users are already friends
            String checkSentQuery = "SELECT * FROM friendrequest WHERE user_id = ? AND username = ? AND requestSent LIKE ?";
            PreparedStatement checkSentStatement = connection.prepareStatement(checkSentQuery);
            checkSentStatement.setInt(1, user_id);
            checkSentStatement.setString(2, username);
            checkSentStatement.setString(3, "%" + sendRequestUsername + "%");
            ResultSet checkSentResult = checkSentStatement.executeQuery();

            if (checkSentResult.next()) { //this check all column that has same id and name to determine whether there has a row that request sent has that  user or that user has be friends
                String requestSent = checkSentResult.getString("requestSent");
                // Check if the friend request has already been sent
                if (requestSent != null && requestSent.contains(sendRequestUsername)) {
                    return false;
                }

                // Update the requestSent column for the sender user
                String updateSenderQuery = "UPDATE friendrequest SET requestSent = CONCAT(requestSent, ?) WHERE user_id = ?";
                PreparedStatement updateSenderStatement = connection.prepareStatement(updateSenderQuery);
                String updatedRequestSent = requestSent != null && !requestSent.contains(sendRequestUsername) ? ";" + sendRequestUsername : "";
                updateSenderStatement.setString(1, updatedRequestSent);
                updateSenderStatement.setInt(2, user_id);
                updateSenderStatement.executeUpdate();

                // Check if the sender user exists in the table
                String checkRequestQuery = "SELECT * FROM friendrequest WHERE user_id = ?";
                PreparedStatement checkRequestStatement = connection.prepareStatement(checkRequestQuery);
                checkRequestStatement.setInt(1, getUserId(sendRequestUsername));
                ResultSet checkRequestResult = checkRequestStatement.executeQuery();

                if(checkRequestResult.next()) {
                    String requestReceived = checkRequestResult.getString("requestReceived");
                    // Check if the friend request has already been received
                    if (requestReceived != null && requestReceived.contains(username)) {
                        return false;
                    }
                    // Update the requestReceived column for the receiver user
                    String updateReceivedQuery = "UPDATE friendrequest SET requestReceived = CONCAT(requestReceived, ?) WHERE user_id = ?";
                    PreparedStatement updateReceivedStatement = connection.prepareStatement(updateReceivedQuery);
                    String updatedRequestReceived = requestReceived != null && !requestReceived.contains(sendRequestUsername) ? ";" + username : "";
                    updateReceivedStatement.setString(1, updatedRequestReceived);
                    updateReceivedStatement.setInt(2, getUserId(sendRequestUsername));
                    updateReceivedStatement.executeUpdate();
                }

                else {

                    // Insert a new row for the receiver user and store the friend request in the requestReceived column
                    String insertReceiverQuery = "INSERT INTO friendrequest (user_id, username, requestReceived) VALUES (?, ?, ?)";
                    PreparedStatement insertReceiverStatement = connection.prepareStatement(insertReceiverQuery);
                    insertReceiverStatement.setInt(1, getUserId(sendRequestUsername));
                    insertReceiverStatement.setString(2, sendRequestUsername);
                    insertReceiverStatement.setString(3, username + ";");
                    insertReceiverStatement.executeUpdate();
                }
            }
            else {
                // Check if the sender user exists in the table
                String checkSenderQuery = "SELECT * FROM friendrequest WHERE user_id = ?";
                PreparedStatement checkSenderStatement = connection.prepareStatement(checkSenderQuery);
                checkSenderStatement.setInt(1, getUserId(username));
                ResultSet checkSenderResult = checkSenderStatement.executeQuery();

                if (checkSenderResult.next()) { //this check is it the user that send request already exist
                    String requestSent = checkSenderResult.getString("requestSent");
                    // Update the existing row for the sender user with the friend request in the requestSent column
                    String updateSenderQuery = "UPDATE friendrequest SET requestSent = CONCAT(requestSent, ?) WHERE user_id = ?";
                    PreparedStatement updateSenderStatement = connection.prepareStatement(updateSenderQuery);
                    String updatedRequestSent = requestSent != null && !requestSent.contains(sendRequestUsername) ? ";" + sendRequestUsername : "";
                    updateSenderStatement.setString(1, updatedRequestSent);
                    updateSenderStatement.setInt(2, getUserId(username));
                    updateSenderStatement.executeUpdate();
                }

                else{
                    // Insert a new row for the sender user with the friend request in the requestSent column
                    String insertSenderQuery = "INSERT INTO friendrequest (user_id, username, requestSent) VALUES (?, ?, ?)";
                    PreparedStatement insertSenderStatement = connection.prepareStatement(insertSenderQuery);
                    insertSenderStatement.setInt(1, getUserId(username));
                    insertSenderStatement.setString(2, username);
                    insertSenderStatement.setString(3, sendRequestUsername + ";");
                    insertSenderStatement.executeUpdate();
                }
                // Check if the sender user exists in the table
                String checkRequestQuery = "SELECT * FROM friendrequest WHERE user_id = ?";
                PreparedStatement checkRequestStatement = connection.prepareStatement(checkRequestQuery);
                checkRequestStatement.setInt(1, getUserId(sendRequestUsername));
                ResultSet checkRequestResult = checkRequestStatement.executeQuery();

                if (checkRequestResult.next()) {
                    String requestReceived = checkRequestResult.getString("requestReceived");
                    // Check if the friend request has already been received
                    if (requestReceived != null && requestReceived.contains(username)) {
                        return false;
                    }
                    // Update the requestReceived column for the receiver user
                    String updateReceivedQuery = "UPDATE friendrequest SET requestReceived = CONCAT(requestReceived, ?) WHERE user_id = ?";
                    PreparedStatement updateReceivedStatement = connection.prepareStatement(updateReceivedQuery);
                    String updatedRequestReceived = requestReceived != null && !requestReceived.contains(sendRequestUsername) ? ";" + username : "";
                    updateReceivedStatement.setString(1, updatedRequestReceived);
                    updateReceivedStatement.setInt(2, getUserId(sendRequestUsername));
                    updateReceivedStatement.executeUpdate();
                } else {

                    // Insert a new row for the receiver user and store the friend request in the requestReceived column
                    String insertReceiverQuery = "INSERT INTO friendrequest (user_id, username, requestReceived) VALUES (?, ?, ?)";
                    PreparedStatement insertReceiverStatement = connection.prepareStatement(insertReceiverQuery);
                    insertReceiverStatement.setInt(1, getUserId(sendRequestUsername));
                    insertReceiverStatement.setString(2, sendRequestUsername);
                    insertReceiverStatement.setString(3, username); //+";"
                    insertReceiverStatement.executeUpdate();
                }
            }

            return true;


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private int getUserId(String username) {
        tempDatabase db = new tempDatabase();
        Connection connection = db.getConnection();

        try {
            String query = "SELECT user_id FROM userdata WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                return result.getInt("user_id");
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

        return -1;
    }

    private List<String> performSearch(String query) {
        List<String> searchResults = new ArrayList<>();
        tempDatabase db = new tempDatabase();
        // Perform the database query to retrieve matching usernames
        String searchQuery = "SELECT username FROM userdata WHERE username LIKE ?";
        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(searchQuery)) {
            statement.setString(1, query + "%");
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
}
