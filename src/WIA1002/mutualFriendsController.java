package WIA1002;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class mutualFriendsController {

    @FXML
    private Button findMutualFriendsButton;

    @FXML
    private ListView<String> mutualFriendsListView;

    @FXML
    private Label resultLabel;

    @FXML
    private Button cancelButton;

    private regularUser user;

    private String selectedUserName;

    private MutualFriendsGraph mutualFriendsGraph;

    public mutualFriendsController() {
        mutualFriendsGraph = new MutualFriendsGraph();
    }

    public void setUser(regularUser user,String selectedUserName) {
        this.user = user;
        this.selectedUserName = selectedUserName;
        findMutualFriendsOnAction();
    }

    @FXML
    private void initialize() {
    }

    @FXML
    private void findMutualFriendsOnAction() {
        tempDatabase db = new tempDatabase();
        String sql = "SELECT sender_id, receiver_id FROM friendrequest WHERE status= 'accepted' ";


        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            // Retrieve friend requests from the database
            ResultSet rs = statement.executeQuery(sql);

            // Create a graph and add vertices
            Graph<String, String> graph = new Graph<>();
            while (rs.next()) {
                int sender = rs.getInt("sender_id");
                int receiver = rs.getInt("receiver_id");

                //add the vertex to the graph using usernamae
                graph.addVertex(db.getUsernameByUserID(sender));
                graph.addVertex(db.getUsernameByUserID(receiver));

                graph.addUndirectedEdge(db.getUsernameByUserID(sender), db.getUsernameByUserID(receiver), "Friend");

            }

            // To update the graph so that the graph contain the above vertex and edges
            mutualFriendsGraph.setGraph(graph);

            graph.printEdges();


            ArrayList<String> mutualFriends = mutualFriendsGraph.findMutualFriends(user.getUsername(), selectedUserName);

            if (mutualFriends.isEmpty()) {
                displayAlert(Alert.AlertType.INFORMATION, "No Results", "No mutual friends found between "+user.getUsername()+" and "+ selectedUserName);

            }
            else {
                // Update UI components
                mutualFriendsListView.getItems().clear();
                mutualFriendsListView.getItems().addAll(mutualFriends);
                resultLabel.setText("Found " + mutualFriends.size() + " mutual friends.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void cancelButtonOnAction() {
        // Close the show mutual friend screen
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
