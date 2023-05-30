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
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
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
    private Button viewProfileButton;
    @FXML
    private TextField searchTextField;
    @FXML
    private Button searchButton;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Button adminLogOutButton;

    private ObservableList<regularUser> regularUsersList;
    private tempDatabase database;
    private regularUser user;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = new tempDatabase();
        regularUsersList = FXCollections.observableArrayList();
        setupTableView();
        loadRegularUsers();
    }
    public TableView<regularUser> getTableView() {
        return tableView;
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

        try (Connection connection = database.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT time_stamp, user_id, email_address, name, username, contact_number, isAdmin FROM userdata")) {

            while (resultSet.next()) {
                int isAdmin = resultSet.getInt("isAdmin");

                // Check if isAdmin is equal to 1, and skip the row if true
                if (isAdmin == 1) {
                    continue;
                }
                LocalDateTime timeStamp = resultSet.getObject("time_stamp", LocalDateTime.class);
                int userId = resultSet.getInt("user_id");
                String emailAddress = resultSet.getString("email_address");
                String name = resultSet.getString("name");
                String username = resultSet.getString("username");
                String contactNumber = resultSet.getString("contact_number");

                regularUser.RegularUserBuilder userBuilder = (regularUser.RegularUserBuilder) new regularUser.RegularUserBuilder()
                        .userId(userId)
                        .email(emailAddress)
                        .name(name)
                        .username(username)
                        .contactNumber(contactNumber)
                        .timeStamp(timeStamp);


                regularUser user = userBuilder.build();
                regularUsersList.add(user);
            }

            tableView.setItems(regularUsersList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void viewProfileButtonClicked() {
        regularUser selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                // Load the profile window FXML file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("profileWindow.fxml"));
                Parent root = loader.load();

                // Get the controller for the profile window
                ProfileWindowController profileController = loader.getController();

                // Set the selected user and display their details
                profileController.setSelectedUser(selectedUser);
                profileController.setTableView(tableView);
                profileController.displayUserDetails();

                // Create a new window and set its properties
                Stage profileStage = new Stage();
                profileStage.setTitle("User Profile");
                profileStage.setScene(new Scene(root));

                // Show the profile window
                profileStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    private void searchButtonClicked() {
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

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT " +
                     "time_stamp, user_id, email_address, name, username, contact_number " +
                     "FROM userdata" +
                     " WHERE ((time_stamp BETWEEN ? AND ?) OR ? IS NULL OR ? IS NULL) " +
                     "AND (user_id LIKE ? OR username LIKE ?)")) {

            statement.setObject(1, startDate);
            statement.setObject(2, endDate);
            statement.setObject(3, startDate);
            statement.setObject(4, endDate);

            String searchPattern = "%" + searchText + "%";
            statement.setString(5, searchPattern);
            statement.setString(6, searchPattern);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                LocalDateTime timeStamp = resultSet.getObject("time_stamp", LocalDateTime.class);
                int userId = resultSet.getInt("user_id");
                String emailAddress = resultSet.getString("email_address");
                String name = resultSet.getString("name");
                String username = resultSet.getString("username");
                String contactNumber = resultSet.getString("contact_number");

                regularUser.RegularUserBuilder userBuilder = (regularUser.RegularUserBuilder) new regularUser.RegularUserBuilder()
                        .userId(userId)
                        .email(emailAddress)
                        .name(name)
                        .username(username)
                        .contactNumber(contactNumber)
                        .timeStamp(timeStamp);

                regularUser user = userBuilder.build();
                regularUsersList.add(user);
            }

            tableView.setItems(regularUsersList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void adminlogoutButtonOnAction(ActionEvent event) {
        // Perform logout operations here

        // Show the login window
        try {
            Parent loginParent = FXMLLoader.load(getClass().getResource("login.fxml"));
            Scene loginScene = new Scene(loginParent);
            Stage loginStage = new Stage();
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.setScene(loginScene);
            loginStage.show();

            // Close the dashboard window
            Stage dashboardStage = (Stage) adminLogOutButton.getScene().getWindow();
            dashboardStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}

