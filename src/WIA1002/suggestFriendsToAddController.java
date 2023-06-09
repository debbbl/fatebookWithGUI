package WIA1002;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class suggestFriendsToAddController implements Initializable {
    @FXML
    private ListView<regularUser> friendListView;

    @FXML
    private Button viewProfileButton;

    @FXML
    private Button addFriendButton;

    private ObservableList<regularUser> suggestedFriends;

    private regularUser user;
    private tempDatabase db = new tempDatabase();

    public void setUser(regularUser user){
        this.user = user;
        loadFriendSuggestions();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        suggestedFriends = FXCollections.observableArrayList();

        // Set the suggested friends as the data source for the ListView
        friendListView.setItems(suggestedFriends);

        // Set the cell factory to display the username
        friendListView.setCellFactory(new Callback<ListView<regularUser>, ListCell<regularUser>>() {
            @Override
            public ListCell<regularUser> call(ListView<regularUser> listView) {
                return new ListCell<regularUser>() {
                    @Override
                    protected void updateItem(regularUser friend, boolean empty) {
                        super.updateItem(friend, empty);
                        if (friend != null) {
                            // Set the username and number of mutual friends
                            setText(friend.getUsername() + " (" + friend.getMutualConnectionsCount() + " mutual friends)");
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });

        // Set the event handler for selecting a friend
        friendListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Enable the buttons when a friend is selected
                viewProfileButton.setDisable(false);
                addFriendButton.setDisable(false);
            } else {
                // Disable the buttons when no friend is selected
                viewProfileButton.setDisable(true);
                addFriendButton.setDisable(true);
            }
        });
    }

    public void loadFriendSuggestions() {
        suggestedFriends.clear();

        // Get suggested friend list
        List<regularUser> suggestedFriendsList = db.getSuggestedFriendList(user.getUsername());

        // Filter out duplicates and existing friends
        Set<regularUser> filteredSuggestions = new HashSet<>();
        for (regularUser friend : suggestedFriendsList) {
            if (!friend.getUsername().equals(user.getUsername()) &&
                    !db.getUserFriendList1(user.getUsername()).contains(friend) &&
                    !filteredSuggestions.contains(friend)) {
                filteredSuggestions.add(friend);
            }
        }

        // Calculate and set the mutual connections count for each friend
        for (regularUser friend : filteredSuggestions) {
            int mutualConnectionsCount = db.getMutualConnectionsCount(user.getUsername(), friend.getUsername());
            friend.setMutualConnectionsCount(mutualConnectionsCount);
        }

        // Add the filtered suggestions to the suggestion list
        suggestedFriends.addAll(filteredSuggestions);

        // Sort suggested friends based on mutual connections count
        suggestedFriends.sort(Comparator.comparingInt(regularUser::getMutualConnectionsCount).reversed());
    }

    public int calculateMutualFriendsCount(regularUser userA, regularUser userB) {
        List<regularUser> userAFriends = db.getUserFriendList1(userA.getUsername());
        List<regularUser> userBFriends = db.getUserFriendList1(userB.getUsername());

        // Find the intersection of userA's and userB's friend lists
        List<regularUser> mutualFriends = userAFriends.stream()
                .filter(userBFriends::contains)
                .collect(Collectors.toList());

        return mutualFriends.size();
    }

    @FXML
    private void ViewProfileButtonClicked() {
        regularUser selectedFriend = friendListView.getSelectionModel().getSelectedItem();
        if (selectedFriend != null) {
            // Code to view the profile of the selected friend
        }
    }

    @FXML
    private void sendFriendRequestButtonClicked() {
        regularUser selectedFriend = friendListView.getSelectionModel().getSelectedItem();
        if (selectedFriend != null) {
            // Code to add the selected friend as a friend
        }
    }


}
