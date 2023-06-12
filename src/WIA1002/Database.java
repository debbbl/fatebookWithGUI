package WIA1002;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.ByteArrayInputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class Database {
    public Connection databaseLink;
    String databaseName = "facebook";
    String databaseUser = "root";
    String databasePassword = "facebookds";
    String url = "jdbc:mysql://localhost:3306/facebook";

    public Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }

        return databaseLink;
    }
    public ObservableList<regularUser> loadRegularUsers() {
        ObservableList<regularUser> regularUsersList = FXCollections.observableArrayList();

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT time_stamp, user_id, email_address, name, username, contact_number, isAdmin FROM userdata")) {

            while (resultSet.next()) {
                int isAdmin = resultSet.getInt("isAdmin");

                // Check if isAdmin is equal to 1, and skip the row if true
                if (isAdmin == 1) {
                    continue;
                }
                LocalDateTime timeStamp = resultSet.getObject("time_stamp", LocalDateTime.class);
                int userId = resultSet.getInt("user_id");
                String emailAddress = resultSet.getString("email_address");
                String name = resultSet.getString("name");
                String username = resultSet.getString("username");
                String contactNumber = resultSet.getString("contact_number");

                regularUser.RegularUserBuilder userBuilder = new regularUser.RegularUserBuilder()
                        .userId(userId)
                        .email(emailAddress)
                        .name(name)
                        .username(username)
                        .contactNumber(contactNumber)
                        .timeStamp(timeStamp);


                regularUser user = userBuilder.build();
                regularUsersList.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            e.getCause();
            e.getMessage();
        }

        return regularUsersList;
    }

    public void updateRegularUser(regularUser user) {
        String updateQuery = "UPDATE userdata SET email_address=?, name=?, " +
                "contact_number=?, birthday=?, gender=?, job=?, hobbies=?, address=?,relationship_status = ? WHERE username=?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {

            statement.setString(1, user.getEmail());
            statement.setString(2, user.getName());
            statement.setString(3, user.getContactNumber());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            statement.setString(5, String.valueOf(user.getGender()));
            statement.setString(6, String.join(", ", user.getJobExperience()));
            statement.setString(7, String.join(", ", user.getHobbies()));
            statement.setString(8, user.getAddress());
            statement.setString(9,user.getRelationshipStatus());
            statement.setString(10, user.getUsername());

            statement.executeUpdate();

            System.out.println("User details updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getUserIdByUsername(String username) {
        int userId = -1;
        String query = "SELECT user_id FROM userdata WHERE username = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                userId = resultSet.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userId;
    }
    
    public String getUsernameByUserID(int userID) {
        String username = null;
        String query = "SELECT username FROM userdata WHERE user_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                username = resultSet.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return username;
    }

    public void updateProfilePicture(String username, byte[] profilePicData) {
        int userId = getUserIdByUsername(username);
        String updateQuery = "UPDATE userdata SET profile_pic=? WHERE user_id=?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {

            statement.setBinaryStream(1, new ByteArrayInputStream(profilePicData));
            statement.setInt(2, userId);

            statement.executeUpdate();

            System.out.println("Profile picture updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public byte[] getProfilePicture(String username) {
        byte[] profilePicData = null;
        String query = "SELECT profile_pic FROM userdata WHERE username = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Blob profilePicBlob = resultSet.getBlob("profile_pic");
                if (profilePicBlob != null) {
                    profilePicData = profilePicBlob.getBytes(1, (int) profilePicBlob.length());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return profilePicData;
    }

    public void updateJob(String username, String job) {
        String updateQuery = "UPDATE userdata SET job = ? WHERE username = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {

            statement.setString(1, job);
            statement.setString(2, username);
            statement.executeUpdate();

            System.out.println("Job updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the database update error
        }
    }

    public void deleteUser(String username) {
        String deleteQuery = "DELETE FROM userdata WHERE username = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setString(1, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertHobby(String hobby) {
        String insertQuery = "INSERT INTO hobbies (hobby_name) VALUES (?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setString(1, hobby);
            statement.executeUpdate();

            System.out.println("Hobby inserted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getHobbiesFromDatabase() {
        List<String> hobbies = new ArrayList<>();
        String query = "SELECT hobby_name FROM hobbies";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String hobby = resultSet.getString("hobby_name");
                hobbies.add(hobby);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hobbies;
    }

    public List<String> getFriendRequestsReceived(int userId) {
        Stack<String> friendRequests = new Stack<>();
        String query = "SELECT sender_id FROM friendrequest WHERE receiver_id = ? AND status = 'pending' ORDER BY timestamp DESC";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int senderId = resultSet.getInt("sender_id");
                String senderUsername = getUsernameByUserId(senderId);
                friendRequests.push(senderUsername);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<String> sortedFriendRequests = new ArrayList<>(friendRequests);

        return sortedFriendRequests;
    }

    private String getUsernameByUserId(int userId) {
        String query = "SELECT username FROM userdata WHERE user_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean sendFriendRequest(int senderId, String senderUsername, String receiverUsername) throws SQLException {

        try {Connection connection = getConnection();
            // Retrieve the user IDs based on the provided usernames
            int senderUserId = getUserIdByUsername(senderUsername);
            int receiverUserId = getUserIdByUsername(receiverUsername);

            // Check if the friend request has already been sent or if the users are already friends
            String checkSentQuery = "SELECT * FROM friendrequest WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) AND status IN ('Pending', 'Accepted', 'Rejected')";
            PreparedStatement checkSentStatement = connection.prepareStatement(checkSentQuery);
            checkSentStatement.setInt(1, senderUserId);
            checkSentStatement.setInt(2, receiverUserId);
            checkSentStatement.setInt(3, receiverUserId);
            checkSentStatement.setInt(4, senderUserId);
            ResultSet checkSentResult = checkSentStatement.executeQuery();

            if (checkSentResult.next()) {
                // Friend request already sent or users are already friends
                return false;
            } else {
                // Insert the new friend request into the database
                String insertQuery = "INSERT INTO friendrequest (sender_id, receiver_id, status,timestamp) VALUES (?, ?, 'Pending',?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                insertStatement.setInt(1, senderUserId);
                insertStatement.setInt(2, receiverUserId);
                insertStatement.setString(3,String.valueOf(LocalDateTime.now()));
                insertStatement.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            e.getCause();
            return false;
        }
    }
    public void acceptFriendRequest(int userId, int friendId) {
        try (Connection connection = getConnection()) {
            // Insert the first row representing the friendship from the user's perspective
            String insertQuery1 = "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?)";
            PreparedStatement statement1 = connection.prepareStatement(insertQuery1);
            statement1.setInt(1, userId);
            statement1.setInt(2, friendId);
            statement1.executeUpdate();

            // Insert the second row representing the friendship from the friend's perspective
            String insertQuery2 = "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?)";
            PreparedStatement statement2 = connection.prepareStatement(insertQuery2);
            statement2.setInt(1, friendId);
            statement2.setInt(2, userId);
            statement2.executeUpdate();

            // Update the status of the friend request to "accepted"
            String updateQuery = "UPDATE friendrequest SET status = 'accepted' WHERE sender_id = ? AND receiver_id = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setInt(1, friendId);
                updateStatement.setInt(2, userId);
                updateStatement.executeUpdate();
            }

            // Use COALESCE so if num_of_friend is null, set to 1, and if it is not null, increase the value by 1 for currentUser
            String incrementQueryUser = "UPDATE userdata SET num_of_friend = COALESCE(num_of_friend + 1, 1) WHERE user_id = ?";
            try (PreparedStatement incrementStatement = connection.prepareStatement(incrementQueryUser)) {
                incrementStatement.setInt(1, userId);
                incrementStatement.executeUpdate();
            }

            // For the sender user
            String incrementQueryFriendId = "UPDATE userdata SET num_of_friend = COALESCE(num_of_friend + 1, 1) WHERE user_id = ?";
            try (PreparedStatement incrementStatement = connection.prepareStatement(incrementQueryFriendId)) {
                incrementStatement.setInt(1, friendId);
                incrementStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void rejectFriendRequest(int userId, String friendUsername) {
        try (Connection connection = getConnection()) {
            // Update the status of the friend request to "rejected"
            String updateQuery = "UPDATE friendrequest SET status = 'rejected' WHERE sender_id = ? AND receiver_id = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setInt(1, getUserIdByUsername(friendUsername));
                updateStatement.setInt(2, userId);
                updateStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void insertChatMessage(int senderId, int receiverId, String message) {
        String insertQuery = "INSERT INTO chats (sender_id, receiver_id, message, timestamp) VALUES (?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setInt(1, senderId);
            statement.setInt(2, receiverId);
            statement.setString(3, message);
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            statement.executeUpdate();

            System.out.println("Chat message inserted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ChatMessage> getChatMessages(int senderId, int receiverId) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        String query = "SELECT sender_id, receiver_id, message, timestamp FROM chats WHERE " +
                "(sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) ORDER BY timestamp";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, senderId);
            statement.setInt(2, receiverId);
            statement.setInt(3, receiverId);
            statement.setInt(4, senderId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int dbSenderId = resultSet.getInt("sender_id");
                int dbReceiverId = resultSet.getInt("receiver_id");
                String message = resultSet.getString("message");
                LocalDateTime timestamp = resultSet.getTimestamp("timestamp").toLocalDateTime();
                String fileType = resultSet.getString("file_type");
                String fileName = resultSet.getString("file_name");
                byte[] fileData = resultSet.getBytes("file_data");

                String dbSender = getUsernameByUserID(dbSenderId);
                String dbReceiver = getUsernameByUserID(dbReceiverId);

                ChatMessage chatMessage = new ChatMessage(dbSender, dbReceiver, message, timestamp);
                chatMessages.add(chatMessage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            e.getCause();
        }

        return chatMessages;
    }
    public List<regularUser> getUserFriendList1(String username) {
        List<regularUser> friendList = new ArrayList<>();
        String query = "SELECT * FROM userdata " +
                "WHERE user_id IN (SELECT friend_id FROM friendship WHERE user_id = " +
                "(SELECT user_id FROM userdata WHERE username = ?))";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                regularUser.RegularUserBuilder userBuilder = new regularUser.RegularUserBuilder();
                userBuilder.username(resultSet.getString("username"))
                        .email(resultSet.getString("email_address"))
                        .name(resultSet.getString("name"))
                        .contactNumber(resultSet.getString("contact_number"))
                        .userId(resultSet.getInt("user_id"));

                Date birthday = resultSet.getDate("birthday");
                if (birthday != null) {
                    userBuilder.birthday(birthday.toLocalDate());
                }

                String genderString = resultSet.getString("gender");
                if (genderString != null) {
                    userBuilder.gender(genderString);
                }

                String jobExperienceString = resultSet.getString("job");
                if (jobExperienceString != null) {
                    List<String> jobExperience = Arrays.asList(jobExperienceString.split(", "));
                    Stack<String> jobExperienceStack = new Stack<>();
                    jobExperienceStack.addAll(jobExperience);
                    userBuilder.jobs(jobExperienceStack);
                }

                String hobbiesString = resultSet.getString("hobbies");
                if (hobbiesString != null) {
                    List<String> hobbies = Arrays.asList(hobbiesString.split(", "));
                    userBuilder.hobbies(hobbies);
                }

                String address = resultSet.getString("address");
                if (address != null) {
                    userBuilder.address(address);
                }

                String relationshipStatus = resultSet.getString("relationship_status");
                if (relationshipStatus != null) {
                    userBuilder.relationshipStatus(relationshipStatus);
                }

                regularUser friend = userBuilder.build();
                friendList.add(friend);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return friendList;
    }

    public List<regularUser> getSuggestedFriendList(String username) {
        List<regularUser> friendList = new ArrayList<>();

        List<regularUser> directFriends = getUserFriendList1(username);

        // Retrieve the user's 2nd-degree connections
        List<regularUser> secondDegreeConnections = getSecondDegreeConnections(username);

        // Retrieve the user's 3rd-degree connections
        List<regularUser> thirdDegreeConnections = getThirdDegreeConnections(username);

        // Combine the second-degree and third-degree connections
        List<regularUser> allConnections = new ArrayList<>();
        allConnections.addAll(secondDegreeConnections);
        allConnections.addAll(thirdDegreeConnections);


        // Create a priority queue for friend suggestions, with custom comparator
        PriorityQueue<regularUser> friendSuggestions = new PriorityQueue<>(new Comparator<regularUser>() {
            @Override
            public int compare(regularUser user1, regularUser user2) {
                int mutualCount1 = getMutualConnectionsCount(username, user1.getUsername());
                int mutualCount2 = getMutualConnectionsCount(username, user2.getUsername());

                if (secondDegreeConnections.contains(user1) && secondDegreeConnections.contains(user2)) {
                    // Both users are second-degree connections, sort by mutual count
                    return Integer.compare(mutualCount2, mutualCount1);
                } else if (secondDegreeConnections.contains(user1)) {
                    // User1 is a second-degree connection, prioritize over user2
                    return -1;
                } else if (secondDegreeConnections.contains(user2)) {
                    // User2 is a second-degree connection, prioritize over user1
                    return 1;
                } else {
                    // Both users are third-degree connections, sort by mutual count
                    return Integer.compare(mutualCount2, mutualCount1);
                }
            }
        });

        // Add the suggested friends to the friendSuggestions priority queue, excluding direct friends and existing friends
        Set<String> addedUsernames = new HashSet<>();
        for (regularUser user : allConnections) {
            if (!directFriends.contains(user) && !user.getUsername().equals(username) && !addedUsernames.contains(user.getUsername()) && !isFriendOfUser(username, user.getUsername())) {
                friendSuggestions.offer(user);
                addedUsernames.add(user.getUsername());
            }
        }

        // Convert the friendSuggestions priority queue back to an ArrayList for return
        friendList.addAll(friendSuggestions);

        return friendList;
    }

    public boolean isFriendOfUser(String username, String friendUsername) {
        int userId = getUserIdByUsername(username);
        int friendId = getUserIdByUsername(friendUsername);

        String query = "SELECT * FROM friendship WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, friendId);
            statement.setInt(3, friendId);
            statement.setInt(4, userId);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next(); // Returns true if a row is found, indicating that the users are friends
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<regularUser> getSecondDegreeConnections(String username) {
        List<regularUser> secondDegreeConnections = new ArrayList<>();
        String query = "SELECT * FROM userdata WHERE user_id IN " +
                "(SELECT f2.friend_id FROM friendship f1 " +
                "JOIN friendship f2 ON f1.friend_id = f2.user_id " +
                "WHERE f1.user_id IN " +
                "(SELECT user_id FROM userdata WHERE username = ?))";


        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                regularUser.RegularUserBuilder userBuilder = new regularUser.RegularUserBuilder();
                userBuilder.username(resultSet.getString("username"))
                        .email(resultSet.getString("email_address"))
                        .name(resultSet.getString("name"))
                        .contactNumber(resultSet.getString("contact_number"))
                        .userId(resultSet.getInt("user_id"));

                // Add the user's 2nd-degree connections
                regularUser secondUser = userBuilder.build();
                secondDegreeConnections.add(secondUser);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return secondDegreeConnections;
    }

    public List<regularUser> getThirdDegreeConnections(String username) {
        List<regularUser> thirdDegreeConnections = new ArrayList<>();
        String query = "SELECT * FROM userdata WHERE user_id IN " +
                "(SELECT DISTINCT f3.friend_id " +
                "FROM friendship f1 " +
                "JOIN friendship f2 ON f1.friend_id = f2.user_id " +
                "JOIN friendship f3 ON f2.friend_id = f3.user_id " +
                "WHERE f1.user_id = (SELECT user_id FROM userdata WHERE username = ?) " +
                "  AND f3.friend_id <> (SELECT user_id FROM userdata WHERE username = ?) " +
                "  AND f3.friend_id NOT IN ( " +
                "    SELECT friend_id " +
                "    FROM friendship " +
                "    WHERE user_id = (SELECT user_id FROM userdata WHERE username = ?) " +
                "  ))";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, username);
            statement.setString(3, username);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                regularUser.RegularUserBuilder userBuilder = new regularUser.RegularUserBuilder();
                userBuilder.username(resultSet.getString("username"))
                        .email(resultSet.getString("email_address"))
                        .name(resultSet.getString("name"))
                        .contactNumber(resultSet.getString("contact_number"))
                        .userId(resultSet.getInt("user_id"));

                // Add the user's 3rd-degree connections
                regularUser thirdUser = userBuilder.build();
                thirdDegreeConnections.add(thirdUser);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return thirdDegreeConnections;
    }

    public int getMutualConnectionsCount(String username1, String username2) {
        List<regularUser> user1Friends = getUserFriendList1(username1);
        List<regularUser> user2Friends = getUserFriendList1(username2);

        Set<Integer> user1FriendIds = new HashSet<>();
        for (regularUser friend : user1Friends) {
            user1FriendIds.add(friend.getUserId());
        }

        int mutualConnectionsCount = 0;
        for (regularUser friend : user2Friends) {
            if (user1FriendIds.contains(friend.getUserId())) {
                mutualConnectionsCount++;
            }
        }

        return mutualConnectionsCount;
    }

    public regularUser getUserDetails(String username) {
        regularUser user = null;
        // Retrieve the user details from the database based on the username
        String userDetailsQuery = "SELECT * FROM userdata WHERE username = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(userDetailsQuery)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                regularUser.RegularUserBuilder userBuilder = new regularUser.RegularUserBuilder();
                userBuilder.username(resultSet.getString("username"))
                        .email(resultSet.getString("email_address"))
                        .contactNumber(resultSet.getString("contact_number"))
                        .name(resultSet.getString("name"))
                        .birthday(resultSet.getObject("birthday", LocalDate.class))
                        .gender(resultSet.getString("gender"))
                        .address(resultSet.getString("address"))
                        .relationshipStatus(resultSet.getString("relationship_status"));

                // Similarly, handle other properties
                String job = resultSet.getString("job");
                Stack<String> jobs = new Stack<>();
                jobs.push(job != null ? job : "N/A");
                userBuilder.jobs(jobs);

                // Handle hobbies (assuming it's a comma-separated string)
                String hobbiesString = resultSet.getString("hobbies");
                List<String> hobbies = (hobbiesString != null && hobbiesString.length() > 0)
                        ? Arrays.asList(hobbiesString.split(","))
                        : Collections.emptyList();
                userBuilder.hobbies(hobbies);


                // Handle profile picture
                byte[] profilePicData = resultSet.getBytes("profile_pic");
                userBuilder.profilePic(profilePicData);

                user = userBuilder.build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            e.getMessage();
            e.getCause();
        }

        return user;
    }
    public List<String> performSearch(String query) {
        List<String> searchResults = new ArrayList<>();
        // Perform the database query to retrieve matching usernames
        String searchQuery = "SELECT username FROM userdata WHERE username LIKE ? OR name LIKE ? OR user_id LIKE ? OR email_address LIKE ? OR contact_number LIKE ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(searchQuery)) {
            String likeQuery = "%" + query + "%";
            statement.setString(1, likeQuery);
            statement.setString(2, likeQuery);
            statement.setString(3, likeQuery);
            statement.setString(4, likeQuery);
            statement.setString(5, likeQuery);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                searchResults.add(resultSet.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            e.getMessage();
            e.getCause();
        }

        return searchResults;
    }
    public void registerUser(String username, String email, String phone, String password) throws NoSuchAlgorithmException {
        regularUser.RegularUserBuilder userBuilder = (regularUser.RegularUserBuilder) new regularUser.RegularUserBuilder()
                .username(username)
                .email(email)
                .contactNumber(phone)
                .password(password);

        regularUser newUser = userBuilder.build();

        Connection connectDB = getConnection();

        String insertFields = "INSERT INTO userdata( username, email_address, contact_number, password,time_stamp) VALUES(";
        String insertValues = "'" + newUser.getUsername() + "','" + newUser.getEmail() + "','" + newUser.getContactNumber() + "','" + newUser.getPassword() + "','" + LocalDateTime.now() + "')";
        String insertToRegister = insertFields + insertValues;

        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(insertToRegister);

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
            e.getMessage();
        }
    }
    public boolean checkUsernameAvailability(String username) {
        Connection connectDB = getConnection();

        try {
            Statement statement = connectDB.createStatement();
            String query = "SELECT * FROM userdata WHERE username = '" + username + "'";
            ResultSet resultSet = statement.executeQuery(query);

            return !resultSet.next(); // Returns true if the resultSet is empty (username is available)

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
            e.getMessage();
            return false;
        }
    }
    public Object validateLogin(String username, String password) throws NoSuchAlgorithmException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();

            // Prepare the SQL statement
            String verifyLogin = "SELECT * FROM userdata WHERE username = ? AND password = ?";
            statement = connection.prepareStatement(verifyLogin);
            statement.setString(1, username);
            Encryptor encryptor = new Encryptor();
            statement.setString(2, encryptor.encryptString(password));

            // Execute the query
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String retrievedUsername = resultSet.getString("username");
                String retrievedPassword = resultSet.getString("password");
                String email = resultSet.getString("email_address");
                String contactNumber = resultSet.getString("contact_number");

                // Check if the user is an admin
                if (resultSet.getInt("isAdmin") == 1) {
                    Admin.AdminBuilder adminBuilder = (Admin.AdminBuilder) new Admin.AdminBuilder()
                            .username(retrievedUsername)
                            .password(retrievedPassword)
                            .email(email)
                            .contactNumber(contactNumber);

                    // Set other properties specific to admin if needed

                    Admin admin = adminBuilder.build();
                    return admin;
                } else {
                    regularUser.RegularUserBuilder userBuilder = (regularUser.RegularUserBuilder) new regularUser.RegularUserBuilder()
                            .username(username)
                            .password(password)
                            .email(email)
                            .contactNumber(contactNumber);

                    // Check and set other properties
                    String name = resultSet.getString("name");
                    userBuilder.name(name != null ? name : "N/A");

                    String birthday = resultSet.getString("birthday");
                    LocalDate birthdate = (birthday != null) ? LocalDate.parse(birthday) : null;
                    userBuilder.birthday(birthdate);

                    String gender = resultSet.getString("gender");
                    userBuilder.gender(gender != null ? gender : "N/A");

                    // Similarly, handle other properties
                    String job = resultSet.getString("job");
                    Stack<String> jobs = new Stack<>();
                    jobs.push(job != null ? job : "N/A");
                    userBuilder.jobs(jobs);

                    // Handle hobbies (assuming it's a comma-separated string)
                    String hobbiesString = resultSet.getString("hobbies");
                    List<String> hobbies = (hobbiesString != null && hobbiesString.length() > 0)
                            ? Arrays.asList(hobbiesString.split(","))
                            : Collections.emptyList();
                    userBuilder.hobbies(hobbies);

                    String address = resultSet.getString("address");
                    userBuilder.address(address != null ? address : "N/A");

                    // Handle profile picture
                    byte[] profilePicData = resultSet.getBytes("profile_pic");
                    userBuilder.profilePic(profilePicData);

                    int user_id = resultSet.getInt("user_id");
                    userBuilder.userId(user_id);

                    String relationshipStatus = resultSet.getString("relationship_status");
                    userBuilder.relationshipStatus(relationshipStatus != null ? relationshipStatus : "N/A");

                    regularUser user = userBuilder.build();
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<String> findMutualFriends(regularUser user, String selectedUserName) {
        List<String> mutualFriends = new ArrayList<>();

        String sql = "SELECT sender_id, receiver_id FROM friendrequest WHERE status = 'accepted'";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();

            // Create a graph and add vertices
            Graph<String, String> graph = new Graph<>();
            while (rs.next()) {
                int sender = rs.getInt("sender_id");
                int receiver = rs.getInt("receiver_id");

                // Add the vertex to the graph using username
                graph.addVertex(getUsernameByUserID(sender));
                graph.addVertex(getUsernameByUserID(receiver));

                graph.addUndirectedEdge(getUsernameByUserID(sender), getUsernameByUserID(receiver), "Friend");
            }

            // To update the graph so that the graph contains the above vertices and edges
            MutualFriendsGraph mutualFriendsGraph = new MutualFriendsGraph();
            mutualFriendsGraph.setGraph(graph);

            mutualFriends = mutualFriendsGraph.findMutualFriends(user.getUsername(), selectedUserName);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mutualFriends;
    }

}
