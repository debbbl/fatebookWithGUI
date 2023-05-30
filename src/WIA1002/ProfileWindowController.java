package WIA1002;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ProfileWindowController {
    @FXML
    private AnchorPane detailsPane;
    @FXML
    private ImageView profilePictureImageView;
    @FXML
    private Button deleteButton;
    @FXML
    private Button backButton;
    @FXML
    private Label nameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label contactNumberLabel;
    @FXML
    private Label birthdayLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Label genderLabel;
    @FXML
    private Label jobLabel;
    @FXML
    private Label hobbiesLabel;
    @FXML
    private Label relationshipStatusLabel;
    @FXML
    private Label addressLabel;
    private regularUser user;
    private TableView<regularUser> tableView;
    public void setSelectedUser(regularUser user) {
        this.user = user;
    }
    public void setTableView(TableView<regularUser> tableView) {
        this.tableView = tableView;
    }

    @FXML
    private void deleteButtonClicked() {
        regularUser selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            // Display a confirmation dialog to confirm the deletion
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete this user?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Perform the delete operation on the user in the database
                tempDatabase db = new tempDatabase();
                db.deleteUser(selectedUser.getUsername());

                // Remove the deleted user from the table view
                tableView.getItems().remove(selectedUser);

                Alert confirmationAlert = new Alert(Alert.AlertType.INFORMATION);
                confirmationAlert.setTitle("Account Deleted");
                confirmationAlert.setHeaderText(null);
                confirmationAlert.setContentText("The user account has been successfully deleted.");
                confirmationAlert.showAndWait();
            }
        }
    }

    @FXML
    private void backButtonClicked(){
        Stage profileStage = (Stage) backButton.getScene().getWindow();
        profileStage.close();
    }


    private regularUser getUserDetails(String username) {
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
    public void displayUserDetails() {
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
