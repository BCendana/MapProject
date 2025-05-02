// **************************************************************************************************
// Major Project Assignment - CMIS 202
// Name:   Braden Cendana
// Description: A program to create and configure custom maps with custom images.
//
// Requirement Tracking:
// Part 2:
// - LinkedPointList is a basic linked list
// - The SpatialPath class contains a stack as a global variable
// Part 3:
// - PointBST class is a binary search tree specifically for points
// - I use the bst in MapState line 608 in order to stop duplicate named points from being created
// - A HashTable is used in StateController to switch "screens"
// New Features:
// Part 3:
// - Maps can now be saved with different image background
// - Maps can have their image background changed
// - Can add new image backgrounds by copying a file on the computer (must be a jpg)
// **************************************************************************************************

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