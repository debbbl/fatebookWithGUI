package WIA1002;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    @FXML
    private Button cancelButton;
    private regularUser user;
    private List<String> hobbiesList = new ArrayList<>();;
    @FXML
    private ImageView editAccountImageView;
    public void setUser(regularUser user) {
        this.user = user;
        populateFields();
    }
    @FXML
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
        genderTextField.setText(user.getGender());
        jobTextField.setText(user.getCurrentJobExperience());
        hobbiesTextField.setText(String.join(", ", user.getHobbies()));
        addressTextField.setText(user.getAddress());
    }
    @FXML
    private void initialize() {
        // Initialize the hobbies list
        hobbiesList = new ArrayList<>();
        hobbiesList.add("Reading");
        hobbiesList.add("Sports");
        hobbiesList.add("Cooking");

        hobbiesTextField.setOnMouseClicked(event -> showHobbiesDialog());

        File brandingFile = new File("images/vertical.png"); //file for the meta logo
        Image brandingImage = new Image(brandingFile.toURI().toString());
        editAccountImageView.setImage(brandingImage);
    }
    public void setHobbiesList(List<String> hobbiesList) {
        this.hobbiesList = hobbiesList;
    }
    @FXML
    private void showHobbiesDialog() {
        List<String> dialogChoices = new ArrayList<>(hobbiesList);
        dialogChoices.add("Create a new hobby");

        ChoiceDialog<String> dialog = new ChoiceDialog<>(hobbiesList.get(0), dialogChoices);
        dialog.setTitle("Select Hobbies");
        dialog.setHeaderText(null);
        dialog.setContentText("Select or create a new hobby:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(hobby -> {
            if (hobby.equals("Create a new hobby")) {
                showNewHobbyDialog();  // Modified this line
            } else {
                hobbiesTextField.setText(hobby);
            }
        });
    }
    @FXML
    private void showNewHobbyDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Hobby");
        dialog.setHeaderText("Create a new hobby");
        dialog.setContentText("Hobby:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(hobby -> {
            HomeController.getInstance().addHobby(hobby); // Update hobbiesList in HomeController
            hobbiesList.add(hobby); // Add the new hobby to hobbiesList
            hobbiesTextField.setText(hobby);
        });
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
        user.setGender(genderTextField.getText());
        user.addJobExperience(jobTextField.getText());
        user.setHobbies(Arrays.asList(hobbiesTextField.getText().split(", ")));
        user.setAddress(addressTextField.getText());

        // Save the updated user details to the database
        // Update the user details in the database
        tempDatabase database = new tempDatabase();
        database.getConnection();
        database.updateRegularUser(user);
        database.updateJob(user.getUsername(), jobTextField.getText()); // Update the job in the database

        // Close the Edit Account screen
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void cancelButtonOnAction() {
        // Close the Edit Account screen
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}