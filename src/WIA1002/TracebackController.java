package WIA1002;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.LinkedList;

public class TracebackController {

    private LinkedList<Interaction> interactionHistory;
    private ObservableList<String> listViewItems;

    @FXML
    private Button cancelButton;

    @FXML
    private ListView<String> historyListView; // Reference to the ListView in the FXML file

    @FXML
    private void initialize() {
        // Set up the historyListView to use listViewItems as its data source
        listViewItems = FXCollections.observableArrayList();
        historyListView.setItems(listViewItems);
    }

    public TracebackController() {
        interactionHistory = new LinkedList<>();
    }

   public void setHistoryListView(ListView<String> historyListView) {
       this.historyListView = historyListView;
       listViewItems = FXCollections.observableArrayList();
       historyListView.setItems(listViewItems);
   }

    @FXML
    private void cancelButtonOnAction() {
        // Close the Edit Account screen
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void addInteraction(String contentType, String content) {
        Interaction interaction = new Interaction(contentType, content);
        interactionHistory.add(interaction);
        if (listViewItems == null) {
            listViewItems = FXCollections.observableArrayList();
            historyListView.setItems(listViewItems);
        }
        listViewItems.add(interaction.toString()); // Add the interaction to the ListView
    }

    public LinkedList<Interaction> getInteractionHistory() {
        return interactionHistory;
    }

    public boolean isEmpty() {
        return interactionHistory.isEmpty();
    }

    public void clearInteractionHistory() {
        interactionHistory.clear();
        listViewItems.clear(); // Clear the ListView
    }

    // Inner class to represent an interaction
    private class Interaction {
        private String contentType;
        private String content;

        public Interaction(String contentType, String content) {
            this.contentType = contentType;
            this.content = content;
        }

        public String getContentType() {
            return contentType;
        }

        public String getContent() {
            return content;
        }

        @Override
        public String toString() {
            return "[" + contentType + "] " + content;
        }
    }
}
