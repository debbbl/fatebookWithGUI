package WIA1002;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class SettingsController implements Initializable, PageController {
    @FXML
    private TextArea activityHistoryTextArea;
    private regularUser user;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setUser(regularUser user){
        this.user = user;
    }

    @Override
    public void setDashboardController(regularUserDashboardController dashboardController) {
    }

    @FXML
    public void viewHistoryButtonOnAction(ActionEvent actionEvent) {
        if (user != null) {
            LinkedList<String> activityHistory = user.getActivityHistory();
            displayActivityHistory(activityHistory);
        }
    }

    private void displayActivityHistory(LinkedList<String> activityHistory) {
        StringBuilder history = new StringBuilder();
        for (String activity : activityHistory) {
            history.append(activity).append("\n");
        }
        activityHistoryTextArea.setText(history.toString());
    }
}

