package WIA1002;

import java.util.*;

public class MutualFriendsGraph {
    private Graph<String,String> graph;

    public MutualFriendsGraph() {

        graph = new Graph<>();
    }

    public void setGraph(Graph<String, String> graph) {
        this.graph = graph;
    }

    public ArrayList<String> findMutualFriends(String user_username, String user1_username)
    {
        return graph.findMutualFriends(user_username,user1_username);
    }
}
