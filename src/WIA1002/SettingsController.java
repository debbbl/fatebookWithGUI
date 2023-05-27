package WIA1002;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable, PageController {

    @FXML
    private Label settingsLabel;

    private regularUserDashboardController dashboardController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the settings page
        settingsLabel.setText("Welcome to the Settings Page!");
    }

    @Override
    public void setDashboardController(regularUserDashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    /*@FXML
    private void goToHome() {
        dashboardController.loadPage("home.fxml");
    }*/
}

