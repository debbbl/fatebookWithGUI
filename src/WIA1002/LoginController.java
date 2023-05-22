package WIA1002;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.stage.StageStyle;
import javafx.scene.layout.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;
import java.net.URL;
import java.io.File;

public class LoginController implements Initializable {

    @FXML
    private Button cancelButton;
    @FXML
    private Button createAccountButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private ImageView brandingImageView;
    @FXML
    private ImageView lockImageView;
    @FXML
    private TextField passwordTextField;
    @FXML
    private TextField usernameTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        File brandingFile = new File("images/vertical.png"); //file for the meta logo
        Image brandingImage = new Image(brandingFile.toURI().toString());
        brandingImageView.setImage(brandingImage);

        File lockFile = new File("images/Picture1.png"); //file for the lock
        Image lockImage = new Image(lockFile.toURI().toString());
        lockImageView.setImage(lockImage);
    }
    public void loginButtonOnAction(ActionEvent event) {
        if (!usernameTextField.getText().isBlank() && !passwordTextField.getText().isBlank()) {
            regularUser user = validateLogin();
            if (user != null) {
                openHomePage(user);
            } else {
                loginMessageLabel.setText("Invalid login. Please try again.");
            }
        } else {
            loginMessageLabel.setText("Please enter your username and password.");
        }
    }

    public void cancelButtonOnAction(ActionEvent event){
        Stage stage = (Stage)cancelButton.getScene().getWindow();
        stage.close();
    }

    public void createAccountButtonOnAction(ActionEvent event){
        createAccountForm();
    }

    public regularUser validateLogin() {
        tempDatabase connectNow = new tempDatabase();
        Connection connectDB = connectNow.getConnection();

        String verifyLogin = "SELECT * FROM userdata WHERE " +
                "username = '" + usernameTextField.getText() + "' AND " +
                "password = '" + passwordTextField.getText() + "'";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            if (queryResult.next()) {
                regularUser.Builder userBuilder = (regularUser.Builder) new regularUser.Builder()
                        .username(queryResult.getString("username"))
                        .password(queryResult.getString("password"))
                        .email(queryResult.getString("email_address"))
                        .contactNumber(queryResult.getString("contact_number"));

                // Check and set other properties
                String name = queryResult.getString("name");
                userBuilder.name(name != null ? name : "N/A");

                String birthday = queryResult.getString("birthday");
                LocalDate birthdate = (birthday != null) ? LocalDate.parse(birthday) : null;
                userBuilder.birthday(birthdate);

                String gender = queryResult.getString("gender");
                userBuilder.gender(gender != null ? gender : "");

                // Similarly, handle other properties
                String job = queryResult.getString("job");
                Stack<String> jobs = new Stack<>();
                jobs.push(job != null ? job : "N/A");
                userBuilder.jobs(jobs);


                // Handle hobbies (assuming it's a comma-separated string)
                String hobbiesString = queryResult.getString("hobbies");
                List<String> hobbies = (hobbiesString != null && hobbiesString.length() > 0)
                        ? Arrays.asList(hobbiesString.split(","))
                        : Collections.emptyList();
                userBuilder.hobbies(hobbies);

                String address = queryResult.getString("address");
                userBuilder.address(address != null ? address : "N/A");

                // Handle profile picture
                byte[] profilePicData = queryResult.getBytes("profile_pic");
                userBuilder.profilePic(profilePicData);
                regularUser user = userBuilder.build();
                loginMessageLabel.setText("Logged in successfully!");

                return user;
            } else {
                loginMessageLabel.setText("Invalid login. Please try again");
            }

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        return null;
    }


    public void createAccountForm(){
        try{
            Parent root = FXMLLoader.load(getClass().getResource("Register.fxml"));
            Stage registerStage = new Stage();
            registerStage.initStyle(StageStyle.UNDECORATED);
            registerStage.setScene(new Scene(root,520,400));
            registerStage.show();

        }catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }

    }

    private void openHomePage(regularUser user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
            Parent root = loader.load();

            HomeController homeController = loader.getController();
            homeController.setUser(user);

            Stage homeStage = new Stage();
            homeStage.setScene(new Scene(root));
            homeStage.initStyle(StageStyle.UNDECORATED);
            homeStage.show();

            // Close the current login stage
            Stage currentStage = (Stage) cancelButton.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
    }



}
