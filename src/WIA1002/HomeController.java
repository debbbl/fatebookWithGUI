package WIA1002;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class HomeController {
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
    private ImageView profilePicImageView;

    private User user;
    @FXML
    private Button logOutButton;

    public void setUser(User user) {
        this.user = user;
        updateUserInfo();
    }

    private void updateUserInfo() {
        nameLabel.setText(user.getName() != null ? user.getName() : "N/A");
        emailLabel.setText(user.getEmail() != null ? user.getEmail() : "N/A");
        usernameLabel.setText(user.getUsername() != null ? user.getUsername() : "N/A");
        contactNumberLabel.setText(user.getContactNumber() != null ? user.getContactNumber() : "N/A");
        LocalDate birthday = user.getBirthday();
        birthdayLabel.setText(birthday != null ? birthday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A");
        ageLabel.setText(birthday != null ? Integer.toString(calculateAge(birthday)) : "N/A");
        genderLabel.setText(Character.toString(user.getGender()));
        jobLabel.setText(user.getJob() != null ? user.getJob() : "N/A");
        hobbiesLabel.setText(user.getHobbies() != null ? String.join(", ", user.getHobbies()) : "N/A");
        addressLabel.setText(user.getAddress() != null ? user.getAddress() : "N/A");
        if (user.getProfilePic() != null) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(user.getProfilePic());
            Image profileImage = new Image(inputStream);
            profilePicImageView.setImage(profileImage);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditAccount.fxml"));
            Parent root = loader.load();

            // Get the controller instance
            EditAccountController editAccountController = loader.getController();

            // Pass the user object to the Edit Account controller
            editAccountController.setUser(user);

            // Create a new Stage for the Edit Account screen
            Stage editAccountStage = new Stage();
            editAccountStage.setTitle("Edit Account");
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

    @FXML
    private void uploadProfilePictureClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Picture");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(profilePicImageView.getScene().getWindow());
        if (selectedFile != null) {
            try {
                Path sourcePath = selectedFile.toPath();
                Path targetPath = Files.createTempFile("profile", ".png");
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

                byte[] profilePicData = Files.readAllBytes(targetPath);
                user.setProfilePic(profilePicData);
                updateUserInfo();
            } catch (Exception e) {
                e.printStackTrace();
                // Handle profile picture upload error
            }
        }


    }

    @FXML
    private void logOutButtonOnAction() {
        try {
            // Load the FXML file for the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();

            // Create a new Stage for the login screen
            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.setScene(new Scene(root));

            // Show the login screen
            loginStage.show();

            // Close the current home screen
            Stage currentStage = (Stage) logOutButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle error loading the login screen
        }
    }
}