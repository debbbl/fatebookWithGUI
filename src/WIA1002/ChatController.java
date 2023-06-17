package WIA1002;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import java.io.ByteArrayInputStream;

import java.net.URL;
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
    private Database database = new Database();
    private ChatDashboard chatDashboard;
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
            byte[] profilePicData = database.getProfilePicture(selectedFriend.getUsername());
            if (profilePicData != null) {
                selectedFriend.setProfilePic(profilePicData);
                profileImageView.setImage(new Image(new ByteArrayInputStream(profilePicData)));
            }
        }

    }

    public void setFriendListController(ChatDashboard chatDashboard) {
        this.chatDashboard = chatDashboard;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        messageTextArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });

        chatMessages = FXCollections.observableArrayList();

        chatListView.setItems(chatMessages);

    }

    @FXML
    private void loadChatMessages() {
        if (user == null || selectedFriend == null) {
            return;
        }
        List<ChatMessage> messages = database.getChatMessages(user.getUserId(), selectedFriend.getUserId());

        chatMessages.clear();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd | HH.mm a");
        for (ChatMessage message : messages) {
            String senderName = message.getSender();
            String receiverName = message.getReceiver();
            LocalDateTime timestamp = message.getTimestamp();
            String formattedTimestamp = timestamp.format(formatter);
            String textMessage = "[" + formattedTimestamp + "] " + senderName + ": " + message.getMessage();
            chatMessages.add(textMessage);

            chatListView.scrollTo(chatMessages.size() - 1);
        }
    }

    @FXML
    public void sendMessage() {
        String messageText = messageTextArea.getText().trim();
        if (!messageText.isEmpty()) {
            database.insertChatMessage(user.getUserId(), selectedFriend.getUserId(), messageText);

            LocalDateTime timestamp = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd | HH.MM a");
            String formattedTimestamp = timestamp.format(formatter);

            String formattedMessage = "[" + formattedTimestamp + "] " + user.getUsername() + ": " + messageText;
            chatMessages.add(formattedMessage);

            messageTextArea.clear();

            chatListView.scrollTo(chatMessages.size() - 1);
        }
    }
}
