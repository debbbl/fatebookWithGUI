package WIA1002;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class regularUserDashboardController implements Initializable {
    @FXML
    private BorderPane mainPane;
    @FXML
    private Button homeButton;
    @FXML
    private Button searchUserButton;
    @FXML
    private Button addFriendsButton;
    @FXML
    private Button createPostButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button findMutualFriendsButton;
    private regularUser user;
    private List<String> hobbiesList;

    public void setUser(regularUser user) {
        this.user = user;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set the initial page
        loadHomePage();

        // Set event handlers for the navigation buttons
        homeButton.setOnAction(event -> loadHomePage());
        searchUserButton.setOnAction(event -> loadSearchUserPage());
        addFriendsButton.setOnAction((event -> loadAddFriendsPage()));
        createPostButton.setOnAction(event -> loadCreatePostPage());
        settingsButton.setOnAction(event -> loadSettingsPage());
    }

    public void showDashboard(regularUser user) {
        this.user = user;
        loadHomePage();
    }
    @FXML
    public void loadHomePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
            Parent homePage = loader.load();

            // Pass the user to the controller if needed
            if (loader.getController() instanceof HomeController) {
                HomeController homeController = loader.getController();
                homeController.setUser(user);
                homeController.updateUserInfo();
            }

            mainPane.setCenter(homePage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadSearchUserPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("searchUser.fxml"));
            mainPane.setCenter(loader.load());

            if (loader.getController() instanceof searchUserController) {
                searchUserController searchUser = loader.getController();
                searchUser.setUser(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void loadAddFriendsPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addFriends.fxml"));
            mainPane.setCenter(loader.load());

            // Pass the controller instance if needed
            if (loader.getController() instanceof addFriendsController) {
                addFriendsController addfriendcontroller = loader.getController();
                addfriendcontroller.setUser(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void loadCreatePostPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("createPost.fxml"));
            mainPane.setCenter(loader.load());

            // Pass the controller instance if needed
            if (loader.getController() instanceof SettingsController) {
                SettingsController settingsController = loader.getController();
                settingsController.setDashboardController(this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadSettingsPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("settings.fxml"));
            mainPane.setCenter(loader.load());

            // Pass the controller instance if needed
            if (loader.getController() instanceof SettingsController) {
                SettingsController settingsController = loader.getController();
                settingsController.setDashboardController(this);
                settingsController.setUser(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void logoutButtonOnAction(ActionEvent event) {
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
            Stage dashboardStage = (Stage) logoutButton.getScene().getWindow();
            dashboardStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
