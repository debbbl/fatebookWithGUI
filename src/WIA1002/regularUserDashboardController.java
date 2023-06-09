package WIA1002;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;

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
    private Button chatButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button logoutButton;
    private regularUser user;
    @FXML
    private ImageView regularUser;
    @FXML
    private Button backButton;
    private final LinkedList<Parent> pageHistory = new LinkedList<>();
    private final LinkedList<Parent> forwardHistory = new LinkedList<>();

    public void setUser(regularUser user) {
        this.user = user;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File regularUserFile = new File("images/regularUser.png"); //file for the lock
        Image regularUserImage = new Image(regularUserFile.toURI().toString());
        regularUser.setImage(regularUserImage);

        homeButton.setOnAction(event -> loadHomePage());
        searchUserButton.setOnAction(event -> loadSearchUserPage());
        addFriendsButton.setOnAction((event -> loadAddFriendsPage()));
        chatButton.setOnAction(event -> loadChatPage());
        settingsButton.setOnAction(event -> loadSettingsPage());
    }

    @FXML
    private void backButtonOnAction() {
        if (!pageHistory.isEmpty()) {
            Parent previousPage = pageHistory.pop();
            forwardHistory.push((Parent) mainPane.getCenter());
            mainPane.setCenter(previousPage);
        }
    }

    @FXML
    private void forwardButtonOnAction() {
        if (!forwardHistory.isEmpty()) {
            Parent nextPage = forwardHistory.pop();
            pageHistory.push((Parent) mainPane.getCenter());
            mainPane.setCenter(nextPage);
        }
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

            if (loader.getController() instanceof HomeController) {
                HomeController homeController = loader.getController();
                homeController.setUser(user);
                homeController.updateUserInfo();
            }

            mainPane.setCenter(homePage);
            pageHistory.push(homePage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadSearchUserPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("searchUser.fxml"));
            Parent searchUserPage = loader.load();
            mainPane.setCenter(searchUserPage);

            if (loader.getController() instanceof searchUserController) {
                searchUserController searchUser = loader.getController();
                searchUser.setUser(user);
            }

            pageHistory.push(searchUserPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void loadAddFriendsPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addFriends.fxml"));
            Parent addFriendsPage = loader.load();
            mainPane.setCenter(addFriendsPage);
            pageHistory.push(addFriendsPage);

            if (loader.getController() instanceof addFriendsController) {
                addFriendsController addfriendcontroller = loader.getController();
                addfriendcontroller.setUser(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void loadChatPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chatDashboard.fxml"));
            Parent friendListPage = loader.load();
            pageHistory.push(friendListPage);

            Database db = new Database();
            List<regularUser> friendList = db.getUserFriendList1(user.getUsername());

            if (loader.getController() instanceof ChatDashboard) {
                ChatDashboard chatDashboard = loader.getController();
                chatDashboard.setUser(user);
                ObservableList<regularUser> observableFriendList = FXCollections.observableArrayList(friendList);
                chatDashboard.setFriendList(observableFriendList);
            }

            ChatController chatController = new ChatController();
            chatController.setUser(user);

            mainPane.setCenter(friendListPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    public void loadSettingsPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("settings.fxml"));
            Parent settingsPage = loader.load();
            pageHistory.push(settingsPage);
            mainPane.setCenter(settingsPage);

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
        try {
            Parent loginParent = FXMLLoader.load(getClass().getResource("login.fxml"));
            Scene loginScene = new Scene(loginParent);
            Stage loginStage = new Stage();
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.setScene(loginScene);
            loginStage.show();

            Stage dashboardStage = (Stage) logoutButton.getScene().getWindow();
            dashboardStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
