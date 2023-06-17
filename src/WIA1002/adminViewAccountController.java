package WIA1002;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class adminViewAccountController implements Initializable {
    @FXML
    private TableView<regularUser> tableView;
    @FXML
    private TableColumn<regularUser, String> timeStampColumn;
    @FXML
    private TableColumn<regularUser, Integer> userIdColumn;
    @FXML
    private TableColumn<regularUser, String> emailAddressColumn;
    @FXML
    private TableColumn<regularUser, String> nameColumn;
    @FXML
    private TableColumn<regularUser, String> usernameColumn;
    @FXML
    private TableColumn<regularUser, String> contactNumberColumn;
    @FXML
    private TextField searchTextField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Button adminLogOutButton;

    private ObservableList<regularUser> regularUsersList;
    private Database database;
    @FXML
    private ImageView adminImageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File adminFile = new File("images/admin.png"); //file for the lock
        Image adminImage = new Image(adminFile.toURI().toString());
        adminImageView.setImage(adminImage);
        database = new Database();
        regularUsersList = FXCollections.observableArrayList();
        setupTableView();
        loadRegularUsers();
    }
    public TableView<regularUser> getTableView() {
        return tableView;
    }

    @FXML
    private void clearButtonClicked() {
        endDatePicker.getEditor().clear();
        endDatePicker.setValue(null);
        startDatePicker.getEditor().clear();
        startDatePicker.setValue(null);
        loadRegularUsers();

    }

    private void setupTableView() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        emailAddressColumn.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        contactNumberColumn.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        timeStampColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<regularUser, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<regularUser, String> param) {
                LocalDateTime timeStamp = param.getValue().getTimeStamp();
                String formattedTimeStamp = timeStamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                return new SimpleStringProperty(formattedTimeStamp);
            }
        });
    }
    private void loadRegularUsers() {
        regularUsersList.clear();
        regularUsersList.addAll(database.loadRegularUsers());
        tableView.setItems(regularUsersList);
    }

    @FXML
    private void viewProfileButtonClicked() {
        regularUser selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("profileWindow.fxml"));
                Parent root = loader.load();

                ProfileWindowController profileController = loader.getController();

                profileController.setSelectedUser(selectedUser);
                profileController.setTableView(tableView);
                profileController.displayUserDetails();

                Stage profileStage = new Stage();
                profileStage.setTitle("User Profile");
                profileStage.setScene(new Scene(root));

                profileStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    private void searchButtonClicked() throws SQLException {
        String searchText = searchTextField.getText().trim();

        regularUsersList.clear();
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
            LocalDate start = startDatePicker.getValue();
            LocalDate end = endDatePicker.getValue();
            startDate = start.atStartOfDay();
            endDate = end.atStartOfDay().plusDays(1);
        }
        try {
            regularUsersList.addAll(database.searchRegularUsers(startDate, endDate, searchText));
            tableView.setItems(regularUsersList);
        } catch (SQLException e) {
            e.printStackTrace();
            e.getCause();
        }
    }



    @FXML
    public void adminlogoutButtonOnAction(ActionEvent event) {
        try {
            Parent loginParent = FXMLLoader.load(getClass().getResource("login.fxml"));
            Scene loginScene = new Scene(loginParent);
            Stage loginStage = new Stage();
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.setScene(loginScene);
            loginStage.show();

            Stage dashboardStage = (Stage) adminLogOutButton.getScene().getWindow();
            dashboardStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

