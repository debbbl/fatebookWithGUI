package WIA1002;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.security.NoSuchAlgorithmException;

public class MockDataCreation {
    
    Encryptor encryptor = new Encryptor();
    
    private static final int NUM_USERS = 30;
    
    public void createMockData() throws NoSuchAlgorithmException {
        tempDatabase connectNow = new tempDatabase();
        Connection connectDB = connectNow.getConnection();

        // Create and insert normal users
        for (int i = 1; i <= NUM_USERS; i++) {
            String username = "user" + i;
            String email = "user" + i + "@example.com";
            String contactNumber = generateContactNumber();
            String password = encryptor.encryptString(generateRandomString(8));

            insertUserData(connectDB, username, email, contactNumber, password);
        }

        // Create and insert admin accounts
        String adminUsername1 = "admin1";
        String adminPassword1 = encryptor.encryptString("admin1pass");
        String adminEmail1 = "admin1@example.com";
        String adminContactNumber1 = generateContactNumber();
        insertUserData(connectDB, adminUsername1, adminEmail1, adminContactNumber1, adminPassword1);

        String adminUsername2 = "admin2";
        String adminPassword2 = encryptor.encryptString("admin2pass");
        String adminEmail2 = "admin2@example.com";
        String adminContactNumber2 = generateContactNumber();
        insertUserData(connectDB, adminUsername2, adminEmail2, adminContactNumber2, adminPassword2);

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
