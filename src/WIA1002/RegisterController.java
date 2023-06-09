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
import java.util.ResourceBundle;
import java.security.NoSuchAlgorithmException;

public class RegisterController implements Initializable {

    Encryptor encryptor = new Encryptor();
    
    @FXML
    private ImageView registerUI;
    @FXML
    private Button closeButton;
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
    private Parent loginRoot;
    private final Database database = new Database();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        File registerFile = new File("images/regiserUI.png");
        Image registerImage = new Image(registerFile.toURI().toString());
        registerUI.setImage(registerImage);

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

        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        try {
            loginRoot = loginLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            e.getCause();
        }
    }

    public boolean checkUsernameAvailability(String username) {
        return database.checkUsernameAvailability(username);
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

        database.registerUser(username, email, phone, password);

        registrationMessageLabel.setText("Registered successfully!");
        navigateToLoginPage();
    }

    public void navigateToLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();

            LoginController loginController = loader.getController();

            Stage stage = (Stage) closeButton.getScene().getWindow();

            registrationMessageLabel.setVisible(true);
            passwordMessageLabel.setVisible(true);

            Platform.runLater(() -> {
                registrationMessageLabel.setText("Registered successfully!");
                passwordMessageLabel.setText("Password matched");

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
