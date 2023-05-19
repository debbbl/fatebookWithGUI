package WIA1002;
import java.sql.*;
import java.util.Random;

public class MockDataCreation {
    private static final int NUM_USERS = 30;
    public void createMockData() {
        tempDatabase connectNow = new tempDatabase();
        Connection connectDB = connectNow.getConnection();

        // Create and insert normal users
        for (int i = 1; i <= NUM_USERS; i++) {
            String username = "user" + i;
            String email = "user" + i + "@example.com";
            String contactNumber = generateContactNumber();
            String password = generateRandomString(8);

            insertUserData(connectDB, username, email, contactNumber, password);
        }

        System.out.println("Mock data created successfully.");
    }

    private void insertUserData(Connection connectDB, String username, String email, String contactNumber, String password) {
        String insertQuery = "INSERT INTO userdata (username, email_address, contact_number, password) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(insertQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, contactNumber);
            preparedStatement.setString(4, password);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String generateContactNumber() {
        Random random = new Random();
        StringBuilder contactNumber = new StringBuilder("01");

        for (int i = 0; i < 8; i++) {
            int digit = random.nextInt(10);
            contactNumber.append(digit);
        }

        return contactNumber.toString();
    }

    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder randomString = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            randomString.append(randomChar);
        }

        return randomString.toString();
    }
}
