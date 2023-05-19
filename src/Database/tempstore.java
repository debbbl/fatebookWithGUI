package Database;

import Database.Database;

import java.util.*;

public class tempstore {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Database database = new Database();

        // Database.User registration
        System.out.println("=== Database.User Registration ===");

        System.out.print("Enter a username: ");
        String username = scanner.nextLine();

        System.out.print("Enter your email address: ");
        String email = scanner.nextLine();

        System.out.print("Enter your phone number: ");
        String contactNumber = scanner.nextLine();

        String password=null;
        String confirmPassword=null;
        boolean passwordMatch = false;

        while (!passwordMatch) {
            System.out.print("Enter a password: ");
            password = scanner.nextLine();

            System.out.print("Retype your password: ");
            confirmPassword = scanner.nextLine();

            if (password.equals(confirmPassword)) {
                passwordMatch = true;
            } else {
                System.out.println("Passwords do not match. Please try again.");
            }
        }

        User.Builder builder = new User.Builder()
                .username(username)
                .email(email)
                .contactNumber(contactNumber)
                .password(password);

        User user = builder.build();
        boolean registrationSuccess = database.registerUser(user);

        if (registrationSuccess) {
            System.out.println("Database.User registration successful!");
        } else {
            System.out.println("Database.User registration failed.");
            return;
        }

        // Database.User login
        System.out.println("\n=== Database.User Login ===");
        System.out.print("Enter your email address or phone number: ");
        String emailOrPhoneNumber = scanner.nextLine();

        System.out.print("Enter your password: ");
        String loginPassword = scanner.nextLine();

        boolean loginSuccess = database.verifyLogin(emailOrPhoneNumber, loginPassword);

        if (loginSuccess) {
            System.out.println("Database.User login successful!");
        } else {
            System.out.println("Invalid email/phone or password.");
        }
    }
}
