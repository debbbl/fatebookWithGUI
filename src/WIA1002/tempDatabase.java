package WIA1002;
import java.sql.*;
public class tempDatabase {
    public Connection databaseLink;

    public Connection getConnection(){
        String databaseName = "facebook";
        String databaseUser = "root";
        String databasePassword = "facebookds";
        String url = "jdbc:mysql://localhost:3306/facebook";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
        }catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }

        return databaseLink;
    }

    public void updateUser(User user) {
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
            statement.setString(7, user.getJob());
            statement.setString(8, String.join(", ", user.getHobbies()));
            statement.setString(9, user.getAddress());
            statement.setString(10, user.getUsername());

            statement.executeUpdate();

            System.out.println("User details updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
