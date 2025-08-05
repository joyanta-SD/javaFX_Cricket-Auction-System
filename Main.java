package application;

import javafx.application.Application;              // import the javafx class Application
import javafx.fxml.FXMLLoader;                      // to load .fxml files
import javafx.scene.Scene;                          // Scene = container for all UI elements (buttons, layouts etc.)
import javafx.stage.Stage;                          // Stage = main window of JavaFX application
import model.ClubDatabase;
import model.PlayerDatabase;
import model.UserDatabase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    public static Stage primaryStage;
    public static ClubDatabase clubDatabase;
    public static PlayerDatabase playerDatabase;
    private static List<Scene> sceneStack = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        clubDatabase = new ClubDatabase();
        if (clubDatabase == null) {
            System.out.println("Club database is null");
            System.exit(0);
        }

        playerDatabase = new PlayerDatabase();
        if (playerDatabase == null) {
            System.out.println("Error: clubDatabase and playerDatabase are null!");
            System.exit(0);
        }

        UserDatabase.loadUsers();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        setRoot("LoginPage.fxml");

        // for testing:
//        setRoot("AdminDashboard.fxml");
//        setRoot("ViewerDashboard.fxml");
//        setRoot("ClubDashboard.fxml");

        primaryStage.setTitle("CricMart");
        primaryStage.show();
    }


    public static void setRoot(String fxmlFile) {
        try {
            System.out.println("primaryStage: " + primaryStage);
            File newFile = new File("src/view/" + fxmlFile);
            if (newFile == null) {
                System.err.println("FXML file not found: /view/" + fxmlFile);
                return;
            }
            System.out.println("Loading FXML from: " + newFile);
            FXMLLoader loader = new FXMLLoader(newFile.toURI().toURL());

            Scene scene = new Scene(loader.load());
            sceneStack.addLast(scene);
            primaryStage.setScene(scene);
            System.out.println("Scene loaded successfully!");
        } catch (Exception e) {
            System.err.println("Failed to load FXML: " + fxmlFile);
            e.printStackTrace();
        }
    }



    // go to the previous page:
    public static void goBack() {
        if(sceneStack.size() > 1) {
            sceneStack.remove(sceneStack.size() - 1);
            primaryStage.setScene(sceneStack.get(sceneStack.size() - 1));
        }
    }


    // delete the previous scene:
    public static void deletePreviousSceneFromStack() {
        sceneStack.remove(sceneStack.size() - 2);

    }


    // update the databases before exiting the program:
    @Override
    public void stop() throws Exception {
        playerDatabase.uploadInfoToFile();
        clubDatabase.uploadInfoToFile();
        UserDatabase.updateUserDatabase();

    }

    public static void updateDatabase() throws Exception {
        playerDatabase.uploadInfoToFile();
        clubDatabase.uploadInfoToFile();
        UserDatabase.updateUserDatabase();

    }


}
