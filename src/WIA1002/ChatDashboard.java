package WIA1002;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class ChatDashboard implements Initializable {
    @FXML
    private ListView<String> friendListView;

    @FXML
    private Button chatButton;

    private ObservableList<regularUser> usersList; // Modified to hold regularUser objects

    private final Database database = new Database();
    private regularUser user;
    @FXML
    private AnchorPane chatPane;

    public ChatDashboard() {
    }

    public void setUser(regularUser user){
        this.user = user;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the friend list
        ObservableList<String> friendList = FXCollections.observableArrayList();
        usersList = FXCollections.observableArrayList(); // Initialize the users list

        // Set the friend list as the data source for the ListView
        friendListView.setItems(friendList);

        // Set the event handler for selecting a friend
        friendListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Enable the chat button when a friend is selected
            // Disable the chat button when no friend is selected
            chatButton.setDisable(newValue == null);
        });
    }

    public void setFriendList(ObservableList<regularUser> friendList) {
        usersList.setAll(friendList); // Store the list of regularUser objects

        // Create a set to store unique usernames
        Set<String> usernameSet = new HashSet<>();

        // Retrieve the list of usernames from the database and add them to the set
        for (regularUser friend : database.getUserFriendList1(user.getUsername())) {
            usernameSet.add(friend.getUsername());
        }

        // Convert the set of usernames to an observable list
        ObservableList<String> usernames = FXCollections.observableArrayList(usernameSet);

        // Set the usernames as the data source for the ListView
        friendListView.setItems(usernames);
    }




    @FXML
    private void openChatWindow() {
        String selectedFriendUsername = friendListView.getSelectionModel().getSelectedItem();

        if (selectedFriendUsername != null) {
            regularUser selectedFriend = null;

            for (regularUser friend : usersList) {
                if (friend.getUsername().equals(selectedFriendUsername)) {
                    selectedFriend = friend;
                    break;
                }
            }

            if (selectedFriend != null) {
                try {
                    FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("Chat.fxml"));
                    Parent chatRoot = chatLoader.load();
                    ChatController chatController = chatLoader.getController();
                    chatController.setUser(user); // Pass both user and selectedFriend
                    chatController.setSelectedFriend(selectedFriend);// Set the ChatController in the FriendListController
                    chatController.setFriendListController(this); // Added this line
                    user.addActionToHistory("Chatted with "+user.getUsername(), LocalDateTime.now());


                    chatPane.getChildren().clear(); // Clear any existing content in the chatPane
                    chatPane.getChildren().add(chatRoot);
                } catch (IOException e) {
                    e.printStackTrace();
                    e.getCause();
                }
            }
        }
    }


}
