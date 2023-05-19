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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
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
    public void loginButtonOnAction(ActionEvent event){
        if(usernameTextField.getText().isBlank() == false && passwordTextField.getText().isBlank() == false){
            validateLogin();
        }else{
            loginMessageLabel.setText("Please enter your username and password");
        }
    }

    public void cancelButtonOnAction(ActionEvent event){
        Stage stage = (Stage)cancelButton.getScene().getWindow();
        stage.close();
    }

    public void createAccountButtonOnAction(ActionEvent event){
        createAccountForm();
    }

    public void validateLogin(){
        tempDatabase connectNow = new tempDatabase();
        Connection connectDB = connectNow.getConnection();

        String verifyLogin = "SELECT count(1) FROM userdata WHERE " +
                "username = '" + usernameTextField.getText() + "' AND " +
                "password = '" + passwordTextField.getText() + "'";

        try{
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            while(queryResult.next()){
                if(queryResult.getInt(1) == 1){
                    loginMessageLabel.setText("Logged in successfully!");
                }else{
                    loginMessageLabel.setText("Invalid login. Please try again");
                }
            }

        }catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }
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
}
