package WIA1002;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
    @FXML
    private CheckBox isAdminCheckBox;
    private final Database database = new Database();

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
            Object user = database.validateLogin(usernameTextField.getText(), passwordTextField.getText());
            if (user != null) {
                if (user instanceof Admin) {
                    openAdminDashboard();
                } else if (user instanceof regularUser) {
                    openRegularUserDashboard((regularUser) user);
                }
            } else {
                loginMessageLabel.setText("Invalid login. Please try again.");
            }
        } else {
            loginMessageLabel.setText("Please enter your username and password.");
        }
    }


    private void openAdminDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("adminViewAccount.fxml"));
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

    @FXML
    public void createAccountButtonOnAction(ActionEvent event) {
        createAccountForm();
    }

    public Object validateLogin() throws NoSuchAlgorithmException {
        Database connectNow = new Database();
        Connection connectDB = connectNow.getConnection();

        String verifyLogin = "SELECT * FROM userdata WHERE " +
                "username = '" + usernameTextField.getText() + "' AND " +
                "password = '" + encryptor.encryptString(passwordTextField.getText()) + "'";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            if (queryResult.next()) {
                String username = queryResult.getString("username");
                String password = queryResult.getString("password");
                String email = queryResult.getString("email_address");
                String contactNumber = queryResult.getString("contact_number");

                // Check if the user is an admin
                if (queryResult.getInt("isAdmin") == 1) {
                    Admin.AdminBuilder adminBuilder = (Admin.AdminBuilder) new Admin.AdminBuilder()
                            .username(username)
                            .password(password)
                            .email(email)
                            .contactNumber(contactNumber);

                    // Set other properties specific to admin if needed

                    Admin admin = adminBuilder.build();
                    loginMessageLabel.setText("Logged in as admin!");

                    return admin;
                } else {
                    regularUser.RegularUserBuilder userBuilder = (regularUser.RegularUserBuilder) new regularUser.RegularUserBuilder()
                            .username(username)
                            .password(password)
                            .email(email)
                            .contactNumber(contactNumber);

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

                    int user_id = queryResult.getInt("user_id");
                    userBuilder.userId(user_id);

                    String relationshipStatus = queryResult.getString("relationship_status");
                    userBuilder.relationshipStatus(relationshipStatus != null ? relationshipStatus : "N/A");

                    regularUser user = userBuilder.build();
                    loginMessageLabel.setText("Logged in successfully!");

                    return user;
                }
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

            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

