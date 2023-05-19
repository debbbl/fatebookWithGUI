package Database;

import java.sql.*;

public class Database {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/facebook";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "facebookds";

    public boolean registerUser(User user) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO userdata (email_address, password, username, contact_number) VALUES (?, ?, ?, ?)");

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getUsername());
            stmt.setString(4, user.getContactNumber());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean verifyLogin(String emailOrPhoneNumber, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM userdata WHERE (email_address = ? OR contact_number = ?) AND password = ?");

            stmt.setString(1, emailOrPhoneNumber);
            stmt.setString(2, emailOrPhoneNumber);
            stmt.setString(3, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
