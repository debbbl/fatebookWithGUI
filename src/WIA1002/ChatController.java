package WIA1002;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.awt.Desktop;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ChatController implements Initializable {
    @FXML
    private ListView<String> chatListView;
    @FXML
    private TextField messageTextArea;
    @FXML
    private Label friendname;
    private ObservableList<String> chatMessages;
    private regularUser user;
    private regularUser selectedFriend;
    private tempDatabase database = new tempDatabase();
    private FriendListController friendListController;
    @FXML
    private ImageView profileImageView;

    public ChatController() {
    }


    public void setUser(regularUser user) {
        this.user = user;
        loadChatMessages();
    }

    public void setSelectedFriend(regularUser selectedFriend) {
        this.selectedFriend = selectedFriend;
        loadChatMessages();
        if (selectedFriend != null) {
            friendname.setText(selectedFriend.getUsername());
        } else {
            friendname.setText("");
        }

            if (selectedFriend.getProfilePic() != null) {
            profileImageView.setImage(new Image(new ByteArrayInputStream(selectedFriend.getProfilePic())));
        } else {
            // Retrieve profile picture from the database
            byte[] profilePicData = database.getProfilePicture(selectedFriend.getUsername());
            if (profilePicData != null) {
                selectedFriend.setProfilePic(profilePicData);
                profileImageView.setImage(new Image(new ByteArrayInputStream(profilePicData)));
            }
        }

    }

    public void setFriendListController(FriendListController friendListController) {
        this.friendListController = friendListController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set the event handler for sending a message when Enter key is pressed
        messageTextArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });

        // Initialize the chat messages list
        chatMessages = FXCollections.observableArrayList();

        // Set the chat messages list as the data source for the ListView
        chatListView.setItems(chatMessages);

    }

    @FXML
    private void loadChatMessages() {
        if (user == null || selectedFriend == null) {
            return;
        }
        List<ChatMessage> messages = database.getChatMessages(user.getUserId(), selectedFriend.getUserId());

        // Clear previous chat messages
        chatMessages.clear();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd | HH.mm a");
        for (ChatMessage message : messages) {
            String senderName = message.getSender();
            String receiverName = message.getReceiver();
            LocalDateTime timestamp = message.getTimestamp();
            String formattedTimestamp = timestamp.format(formatter);
            String textMessage = "[" + formattedTimestamp + "] " + senderName + ": " + message.getMessage();
            chatMessages.add(textMessage);


            // Scroll to the bottom of the chat
            chatListView.scrollTo(chatMessages.size() - 1);
        }
    }

    @FXML
    public void sendMessage() {
        String messageText = messageTextArea.getText().trim();
        if (!messageText.isEmpty()) {
            // Insert the chat message into the database
            database.insertChatMessage(user.getUserId(), selectedFriend.getUserId(), messageText);

            // Retrieve the timestamp of the inserted message
            LocalDateTime timestamp = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd | HH.MM a");
            String formattedTimestamp = timestamp.format(formatter);

            // Format the message with the formatted timestamp, sender, and message
            String formattedMessage = "[" + formattedTimestamp + "] " + user.getUsername() + ": " + messageText;
            // Add the message to the chat messages list
            chatMessages.add(formattedMessage);

            // Clear the message input area
            messageTextArea.clear();

            // Scroll to the bottom of the chat
            chatListView.scrollTo(chatMessages.size() - 1);
        }
    }
}
