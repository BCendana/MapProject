import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The main class from which the application runs
 */
public class CustomMapApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {

        //Set stage settings
        primaryStage.setTitle("Custom Map");
        //primaryStage.setResizable(false);

        //Create the mainScene and controller
        Scene mainScene = new Scene(new StackPane());
        StateController controller = new StateController(mainScene);

        //Initialize all the different ProgramStates
        MainMenuState mainMenu = new MainMenuState(controller);
        MapState mapState = new MapState(controller);

        //Add the ProgramStates to the controller
        controller.addState("main menu", mainMenu);
        controller.addState("map", mapState);

        //Set everything to render correctly
        controller.setActive("main menu");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}