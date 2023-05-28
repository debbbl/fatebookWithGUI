package WIA1002;

import java.util.*;

public class MutualFriendsGraph {
    private Map<String, Set<String>> graph;

    public MutualFriendsGraph() {
        graph = new HashMap<>();
    }

    public void addFriendship(String user1, String user2) {
        graph.computeIfAbsent(user1, k -> new HashSet<>()).add(user2);
        graph.computeIfAbsent(user2, k -> new HashSet<>()).add(user1);
    }

    public List<String> getMutualFriends(String user1, String user2) {
        Set<String> friends1 = graph.getOrDefault(user1, new HashSet<>());
        Set<String> friends2 = graph.getOrDefault(user2, new HashSet<>());

        Set<String> mutualFriends = new HashSet<>(friends1);
        mutualFriends.retainAll(friends2);

        return new ArrayList<>(mutualFriends);
    }
}
