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
import java.time.LocalDateTime;
import java.util.*;

public class LoginController implements Initializable {
    @FXML
    private Button cancelButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private ImageView loginImageView;
    @FXML
    private TextField passwordTextField;
    @FXML
    private TextField usernameTextField;
    private final Database database = new Database();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File loginFile = new File("images/loginUI.png"); //file for the meta logo
        Image loginImage = new Image(loginFile.toURI().toString());
        loginImageView.setImage(loginImage);
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
                    ((regularUser) user).addActionToHistory("Logged in", LocalDateTime.now());
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

    // Redirect user to register page
    public void createAccountForm() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Register.fxml"));
            Stage registerStage = new Stage();
            registerStage.initStyle(StageStyle.UNDECORATED);
            registerStage.setScene(new Scene(root, 1275, 670));
            registerStage.show();

            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

