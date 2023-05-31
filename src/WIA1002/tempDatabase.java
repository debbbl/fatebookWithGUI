package WIA1002;
import java.io.ByteArrayInputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class tempDatabase {
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

    public void updateRegularUser(regularUser user) {
        String updateQuery = "UPDATE userdata SET email_address=?, name=?, " +
                "contact_number=?, birthday=?, gender=?, job=?, hobbies=?, address=? WHERE username=?";

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
            statement.setString(9, user.getUsername());

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
}