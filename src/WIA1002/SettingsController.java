package WIA1002;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class SettingsController implements Initializable, PageController {
    @FXML
    private TextArea activityHistoryTextArea;
    private regularUser user;
    private regularUserDashboardController dashboardController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setUser(regularUser user){
        this.user = user;
    }

    @Override
    public void setDashboardController(regularUserDashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    public void viewHistoryButtonOnAction(ActionEvent actionEvent) {
        if (user != null) {
            // Access the activity history of the user and display it
            LinkedList<String> activityHistory = user.getActivityHistory();
            displayActivityHistory(activityHistory);
        }
    }

    private void displayActivityHistory(LinkedList<String> activityHistory) {
        // Display the activity history in the TextArea
        StringBuilder history = new StringBuilder();
        for (String activity : activityHistory) {
            history.append(activity).append("\n");
        }
        activityHistoryTextArea.setText(history.toString());
    }
}

