package WIA1002;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private TextField usernameField;
    @FXML
    private Button deleteButton;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TableView<regularUser> userTableView;

    private tempDatabase database;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        database = new tempDatabase();
    }
    @FXML
    private void deleteUserAccount() {
        String username = usernameField.getText();
        if (!username.isEmpty()) {
            database.deleteUserAccount(username);
            displayAlert("User Account Deleted", "User account has been deleted successfully.");
        } else {
            displayAlert("Input Error", "Please enter a username.");
        }
    }

    @FXML
    private void viewNewlyCreatedAccounts() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate != null && endDate != null && startDate.isBefore(endDate)) {
            userTableView.getItems().clear();
            userTableView.getItems().addAll(database.getNewlyCreatedAccounts(startDate, endDate));
        } else {
            displayAlert("Input Error", "Please select valid start and end dates.");
        }
    }

    private void displayAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) userTableView.getScene().getWindow();
        stage.close();
    }
}
