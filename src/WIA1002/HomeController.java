package WIA1002;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HomeController implements Initializable, PageController {
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
    private Label addressLabel;
    @FXML
    private Label relationshipStatusLabel;
    @FXML
    private ImageView profilePictureImageView;
    private regularUser user;
    private final Database database = new Database();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the home page
        updateUserInfo();
    }
    public void setUser(regularUser user){
        this.user = user;
        updateUserInfo();
    }
    @Override
    public void setDashboardController(regularUserDashboardController dashboardController) {
    }

    @FXML
    private void updateProfilePictureOnAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Picture");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(profilePictureImageView.getScene().getWindow());
        if (selectedFile != null) {
            try {
                Path sourcePath = selectedFile.toPath();
                Path targetPath = Files.createTempFile("profile", ".png");
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

                byte[] profilePicData = Files.readAllBytes(targetPath);
                database.updateProfilePicture(user.getUsername(), profilePicData);

                user.setProfilePic(profilePicData);
                updateUserInfo();
                user.addActionToHistory("updated profile picture", LocalDateTime.now());
            } catch (Exception e) {
                e.printStackTrace();
                e.getCause();
            }
        }
    }

    public void updateUserInfo() {
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

    private int calculateAge(LocalDate birthday) {
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthday, currentDate).getYears();
    }
    @FXML
    private void editAccountButtonOnAction() {
        try {
            // Load the FXML file for the Edit Account screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("editAccount.fxml"));
            Parent root = loader.load();

            // Get the controller instance
            EditAccountController editAccountController = loader.getController();

            // Pass the user object to the Edit Account controller
            editAccountController.setUser(user);

            // Create a new Stage for the Edit Account screen
            Stage editAccountStage = new Stage();
            editAccountStage.initStyle(StageStyle.UNDECORATED);
            editAccountStage.setScene(new Scene(root));

            // Show the Edit Account screen
            editAccountStage.showAndWait();

            // Update the user info after editing
            updateUserInfo();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle error loading the Edit Account screen
        }
    }
}

