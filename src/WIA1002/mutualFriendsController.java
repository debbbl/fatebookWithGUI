package WIA1002;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class mutualFriendsController {
    @FXML
    private TextField userTextField;

    @FXML
    private Button findMutualFriendsButton;

    @FXML
    private ListView<String> mutualFriendsListView;

    @FXML
    private Label resultLabel;

    private regularUser user;

    private MutualFriendsGraph mutualFriendsGraph;

    public mutualFriendsController() {
        mutualFriendsGraph = new MutualFriendsGraph();
    }

    public void setUser(regularUser user) {
        this.user = user;
    }

    @FXML
    private void initialize() {
        // Set up any initialization tasks here
    }

    @FXML
    private void findMutualFriendsOnAction() {
        String user1_username = userTextField.getText();
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


            ArrayList<String> mutualFriends = mutualFriendsGraph.findMutualFriends(user.getUsername(), user1_username);
            // Update UI components
            mutualFriendsListView.getItems().clear();
            mutualFriendsListView.getItems().addAll(mutualFriends);
            resultLabel.setText("Found " + mutualFriends.size() + " mutual friends.");

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setMutualFriendsGraph(MutualFriendsGraph mutualFriendsGraph) {
        this.mutualFriendsGraph = mutualFriendsGraph;
    }
}
