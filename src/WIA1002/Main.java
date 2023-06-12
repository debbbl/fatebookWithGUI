package WIA1002;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;

public class Main extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception{
        //create mock data
        MockDataCreation mockDataCreation = new MockDataCreation();
        mockDataCreation.createMockData();

        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(new Scene(root,520,400));
        primaryStage.show();
    }

    public static void main(String[] args){
        tempDatabase database = new tempDatabase();
        database.insertChatMessage(34, 33, "I am fine");
        launch(args);
    }
}