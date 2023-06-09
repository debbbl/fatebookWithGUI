package WIA1002;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.fxml.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.security.NoSuchAlgorithmException;

public class RegisterController implements Initializable {

    Encryptor encryptor = new Encryptor();
    
    @FXML
    private ImageView fateImageView;
    @FXML
    private Button closeButton;
    @FXML
    private Button registerButton;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField phoneNumberTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private TextField confirmPasswordTextField;
    @FXML
    private Label registrationMessageLabel;
    @FXML
    private Label passwordMessageLabel;
    @FXML
    private Label usernameLabel;

    private FXMLLoader loginLoader;
    private Parent loginRoot;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        File fateFile = new File("images/fateLogo2.png"); //file for the fatelogo
        Image fateImage = new Image(fateFile.toURI().toString());
        fateImageView.setImage(fateImage);

        usernameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                boolean isUsernameAvailable = checkUsernameAvailability(newValue);
                if (!isUsernameAvailable) {
                    usernameLabel.setText("This username is not available");
                } else {
                    usernameLabel.setText("");
                }
            }
        });

        loginLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        try {
            loginRoot = loginLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public boolean checkUsernameAvailability(String username) {
        tempDatabase connectNow = new tempDatabase();
        Connection connectDB = connectNow.getConnection();

        try {
            Statement statement = connectDB.createStatement();
            String query = "SELECT * FROM userdata WHERE username = '" + username + "'";
            ResultSet resultSet = statement.executeQuery(query);

            return !resultSet.next(); // Returns true if the resultSet is empty (username is available)

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @FXML
    public void closeButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();

        // Load the login.fxml and set it as the scene
        if (loginRoot != null) {
            Scene loginScene = new Scene(loginRoot);
            Stage loginStage = new Stage();
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.setScene(loginScene);
            loginStage.show();
        }
    }

    @FXML
    public void registerButtonOnAction(ActionEvent event) throws NoSuchAlgorithmException {
        if (passwordTextField.getText().equals(confirmPasswordTextField.getText())) {
            passwordMessageLabel.setText("Password matched");
            registrationMessageLabel.setText("Registered successfully!");
            registerUser();
        }else{
            passwordMessageLabel.setText("Password does not match");
            registrationMessageLabel.setText("");
        }
    }

    public void registerUser() throws NoSuchAlgorithmException {
        String username = usernameTextField.getText();
        String email = emailTextField.getText();
        String phone = phoneNumberTextField.getText();
        String password = encryptor.encryptString(passwordTextField.getText());

        regularUser.RegularUserBuilder userBuilder = (regularUser.RegularUserBuilder) new regularUser.RegularUserBuilder()
                .username(username)
                .email(email)
                .contactNumber(phone)
                .password(password);

        regularUser newUser = userBuilder.build();

        tempDatabase connectNow = new tempDatabase();
        Connection connectDB = connectNow.getConnection();

        String insertFields = "INSERT INTO userdata( username, email_address, contact_number, password,time_stamp) VALUES(";
        String insertValues = "'" + newUser.getUsername() + "','" + newUser.getEmail() + "','" + newUser.getContactNumber() + "','" + newUser.getPassword() + "','" + LocalDateTime.now() + "')";
        String insertToRegister = insertFields + insertValues;

        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(insertToRegister);

            registrationMessageLabel.setText("Registered successfully!");
            navigateToLoginPage();

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public void navigateToLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();

            // Assuming you have a LoginController class for the login page
            LoginController loginController = loader.getController();

            // Code to retrieve the Stage and set the new scene
            Stage stage = (Stage) closeButton.getScene().getWindow();

            // Show the registration message label and password message label
            registrationMessageLabel.setVisible(true);
            passwordMessageLabel.setVisible(true);

            // Update the labels on the JavaFX Application Thread
            Platform.runLater(() -> {
                registrationMessageLabel.setText("Registered successfully!");
                passwordMessageLabel.setText("Password matched");

                // Show the login page after a delay of 2 seconds
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        stage.setScene(new Scene(root));
                        stage.show();
                    }
                }));
                timeline.setCycleCount(1);
                timeline.play();
            });

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }


}
