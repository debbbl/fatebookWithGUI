package WIA1002;

import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    private DatePicker birthdayTextField;
    @FXML
    private TextField genderTextField;
    @FXML
    private TextField jobTextField;
    @FXML
    private TextField hobbiesTextField;
    @FXML
    private TextField addressTextField;
    @FXML
    private TextField relationshipStatusTextField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    private regularUser user;
    private List<String> hobbiesList = new ArrayList<>();;
    private List<String> relationshipStatusOptions = new ArrayList<>(Arrays.asList(
            "Single", "In a relationship", "Engaged", "Married", "In a civil union",
            "In a domestic partnership", "In an open relationship", "It's complicated",
            "Separated", "Divorced", "Widowed"
    ));
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
            birthdayTextField.setValue(user.getBirthday());
        } else {
            birthdayTextField.setValue(null);
        }
        genderTextField.setText(user.getGender());
        jobTextField.setText(user.getCurrentJobExperience());
        hobbiesTextField.setText(String.join(", ", user.getHobbies()));
        addressTextField.setText(user.getAddress());
        relationshipStatusTextField.setText(user.getRelationshipStatus());
        relationshipStatusTextField.setOnMouseClicked(event -> showRelationshipStatusDialog());
    }

    private void showRelationshipStatusDialog() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(user.getRelationshipStatus(), relationshipStatusOptions);
        dialog.setTitle("Select Relationship Status");
        dialog.setHeaderText(null);
        dialog.setContentText("Select your relationship status:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(status -> relationshipStatusTextField.setText(status));
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
            //tempHomeController.getInstance().addHobby(hobby); // Update hobbiesList in HomeController
            hobbiesList.add(hobby); // Add the new hobby to hobbiesList
            hobbiesTextField.setText(hobby);
        });
    }
    @FXML
    private void saveButtonOnAction() {
        try{
            // Update the user details
            user.setEmail(emailTextField.getText());
            user.setName(nameTextField.getText());
            user.setUsername(usernameTextField.getText());
            user.setContactNumber(contactNumberTextField.getText());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate birthday = birthdayTextField.getValue();
            user.setBirthday(birthday);
            user.setGender(genderTextField.getText());
            user.addJobExperience(jobTextField.getText());
            user.setHobbies(Arrays.asList(hobbiesTextField.getText().split(", ")));
            user.setAddress(addressTextField.getText());
            user.setRelationshipStatus(relationshipStatusTextField.getText());

            // Save the updated user details to the database
            // Update the user details in the database
            tempDatabase database = new tempDatabase();
            database.getConnection();
            database.updateRegularUser(user);
            database.updateJob(user.getUsername(), jobTextField.getText()); // Update the job in the database

            // Close the Edit Account screen
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
        }catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }

    }
    @FXML
    private void cancelButtonOnAction() {
        // Close the Edit Account screen
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}