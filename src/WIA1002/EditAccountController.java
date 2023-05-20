package WIA1002;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class EditAccountController {
    @FXML
    private TextField emailTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField contactNumberTextField;

    @FXML
    private TextField birthdayTextField;

    @FXML
    private TextField genderTextField;

    @FXML
    private TextField jobTextField;

    @FXML
    private TextField hobbiesTextField;

    @FXML
    private TextField addressTextField;

    @FXML
    private Button saveButton;

    private User user;

    public void setUser(User user) {
        this.user = user;
        populateFields();
    }

    private void populateFields() {
        emailTextField.setText(user.getEmail());
        nameTextField.setText(user.getName());
        usernameTextField.setText(user.getUsername());
        contactNumberTextField.setText(user.getContactNumber());
        if (user.getBirthday() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String birthday = user.getBirthday().format(formatter);
            birthdayTextField.setText(birthday);
        } else {
            birthdayTextField.setText("");
        }
        genderTextField.setText(Character.toString(user.getGender()));
        jobTextField.setText(user.getJob());
        hobbiesTextField.setText(String.join(", ", user.getHobbies()));
        addressTextField.setText(user.getAddress());
    }

    @FXML
    private void saveButtonOnAction() {
        // Update the user details
        user.setEmail(emailTextField.getText());
        user.setName(nameTextField.getText());
        user.setUsername(usernameTextField.getText());
        user.setContactNumber(contactNumberTextField.getText());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate birthday = LocalDate.parse(birthdayTextField.getText(), formatter);
        user.setBirthday(birthday);
        user.setGender(genderTextField.getText().charAt(0));
        user.setJob(jobTextField.getText());
        user.setHobbies(Arrays.asList(hobbiesTextField.getText().split(", ")));
        user.setAddress(addressTextField.getText());

        // Save the updated user details to the database
        // Update the user details in the database
        tempDatabase database = new tempDatabase();
        database.getConnection();
        database.updateUser(user);

        // Close the Edit Account screen
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
