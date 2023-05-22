package WIA1002;
import java.io.ByteArrayInputStream;
import java.sql.*;
public class tempDatabase {
    public Connection databaseLink;
    String databaseName = "facebook";
    String databaseUser = "root";
    String databasePassword = "facebookds";
    String url = "jdbc:mysql://localhost:3306/facebook";

    public Connection getConnection(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
        }catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }

        return databaseLink;
    }

    public void updateRegularUser(regularUser user) {
        String updateQuery = "UPDATE userdata SET email_address=?, name=?, username=?, " +
                "contact_number=?, birthday=?, gender=?, job=?, hobbies=?, address=? WHERE username=?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {

            statement.setString(1, user.getEmail());
            statement.setString(2, user.getName());
            statement.setString(3, user.getUsername());
            statement.setString(4, user.getContactNumber());
            statement.setDate(5, Date.valueOf(user.getBirthday()));
            statement.setString(6, String.valueOf(user.getGender()));
            statement.setString(7, String.join(", ", user.getJobExperience()));
            statement.setString(8, String.join(", ", user.getHobbies()));
            statement.setString(9, user.getAddress());
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




}
