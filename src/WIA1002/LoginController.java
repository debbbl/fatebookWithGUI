package WIA1002;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

public class LoginController implements Initializable {
    Encryptor encryptor = new Encryptor();
    @FXML
    private Button cancelButton;
    @FXML
    private Button createAccountButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private ImageView brandingImageView;
    @FXML
    private ImageView lockImageView;
    @FXML
    private TextField passwordTextField;
    @FXML
    private TextField usernameTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File brandingFile = new File("images/vertical.png"); //file for the meta logo
        Image brandingImage = new Image(brandingFile.toURI().toString());
        brandingImageView.setImage(brandingImage);

        File lockFile = new File("images/Picture1.png"); //file for the lock
        Image lockImage = new Image(lockFile.toURI().toString());
        lockImageView.setImage(lockImage);
    }

    @FXML
    public void loginButtonOnAction(ActionEvent event) throws NoSuchAlgorithmException {
        if (!usernameTextField.getText().isBlank() && !passwordTextField.getText().isBlank()) {
            regularUser user = validateLogin();
            if (user != null) {
                openRegularUserDashboard(user);
            } else {
                loginMessageLabel.setText("Invalid login. Please try again.");
            }
        } else {
            loginMessageLabel.setText("Please enter your username and password.");
        }
    }

    public void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void openRegularUserDashboard(regularUser user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("regularUserDashboard.fxml"));
            Parent root = loader.load();

            regularUserDashboardController dashboardController = loader.getController();
            dashboardController.setUser(user);
            dashboardController.showDashboard(user);

            Stage dashboardStage = new Stage();
            dashboardStage.setScene(new Scene(root));
            dashboardStage.initStyle(StageStyle.UNDECORATED);
            dashboardStage.show();

            // Close the current login stage
            Stage currentStage = (Stage) cancelButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createAccountButtonOnAction(ActionEvent event) {
        createAccountForm();
    }

    public regularUser validateLogin() throws NoSuchAlgorithmException {
        tempDatabase connectNow = new tempDatabase();
        Connection connectDB = connectNow.getConnection();

        String verifyLogin = "SELECT * FROM userdata WHERE " +
                "username = '" + usernameTextField.getText() + "' AND " +
                "password = '" + encryptor.encryptString(passwordTextField.getText()) + "'";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            if (queryResult.next()) {
                regularUser.RegularUserBuilder userBuilder = (regularUser.RegularUserBuilder) new regularUser.RegularUserBuilder()
                        .username(queryResult.getString("username"))
                        .password(queryResult.getString("password"))
                        .email(queryResult.getString("email_address"))
                        .contactNumber(queryResult.getString("contact_number"));

                // Check and set other properties
                String name = queryResult.getString("name");
                userBuilder.name(name != null ? name : "N/A");

                String birthday = queryResult.getString("birthday");
                LocalDate birthdate = (birthday != null) ? LocalDate.parse(birthday) : null;
                userBuilder.birthday(birthdate);

                String gender = queryResult.getString("gender");
                userBuilder.gender(gender != null ? gender : "N/A");

                // Similarly, handle other properties
                String job = queryResult.getString("job");
                Stack<String> jobs = new Stack<>();
                jobs.push(job != null ? job : "N/A");
                userBuilder.jobs(jobs);

                // Handle hobbies (assuming it's a comma-separated string)
                String hobbiesString = queryResult.getString("hobbies");
                List<String> hobbies = (hobbiesString != null && hobbiesString.length() > 0)
                        ? Arrays.asList(hobbiesString.split(","))
                        : Collections.emptyList();
                userBuilder.hobbies(hobbies);

                String address = queryResult.getString("address");
                userBuilder.address(address != null ? address : "N/A");

                // Handle profile picture
                byte[] profilePicData = queryResult.getBytes("profile_pic");
                userBuilder.profilePic(profilePicData);

                String relationshipStatus = queryResult.getString("relationship_status");
                userBuilder.relationshipStatus(relationshipStatus != null ? relationshipStatus : "N/A");

                regularUser user = userBuilder.build();
                loginMessageLabel.setText("Logged in successfully!");

                return user;
            } else {
                loginMessageLabel.setText("Invalid login. Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                connectDB.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // Redirect user to register page
    public void createAccountForm() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Register.fxml"));
            Stage registerStage = new Stage();
            registerStage.initStyle(StageStyle.UNDECORATED);
            registerStage.setScene(new Scene(root, 520, 400));
            registerStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

