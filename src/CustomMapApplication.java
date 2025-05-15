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
// Part 4:
// - WeightedPointEdge & WeightedPointGraph are used for a Graph data structure
// - A thread is used upon loading starting at line 476 in the MapState file
// - A thread is also used upon saving in MapState
// - Added a settings screen & some basic graphical updates (Other new feature req.)
//
// New Features:
// Part 3:
// - Maps can now be saved with different image background
// - Maps can have their image background changed
// - Can add new image backgrounds by copying a file on the computer (must be a jpg)
// Part 4:
// - Paths can now only be made if there exists an edge between all points on the path
// - Settings allow for changing the default image and setting the RGB color of
//   the point's icons, point's text, path's color, and edge's color.
// - Set a gray background and made the buttons look a bit nicer. Also added a title label.
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

        //This commented out code below is used to make a file to test if the saving/loading
        //threads were working. The file it creates is NOT uploaded with the rest of the project
        //because of its size. I am leaving it here in case you want to test out the thread stuff yourself.
//        FileWriter writer = new FileWriter("src/saves/StressTest.txt");
//        writer.write("res\maps\differentmap.jpg");
//        for(int i = 0; i < 500; i++){
//            for(int j = 0; j < 500; j++){
//                writer.write("point,");
//                writer.write("Point" + i + "-" + j + ",");
//                writer.write((double) i + "");
//                writer.write(",");
//                writer.write((double) j + "");
//                writer.write("\n");
//            }
//        }

        //Set stage settings
        primaryStage.setTitle("Custom Map");
        //primaryStage.setResizable(false);

        //Create the mainScene and controller
        Scene mainScene = new Scene(new StackPane());
        StateController controller = new StateController(mainScene);

        //Load settings
        Configuration.loadConfig();

        //Initialize all the different ProgramStates
        MainMenuState mainMenu = new MainMenuState(controller);
        MapState mapState = new MapState(controller);
        SettingsState settingsState = new SettingsState(controller);

        //Add the ProgramStates to the controller
        controller.addState("main menu", mainMenu);
        controller.addState("map", mapState);
        controller.addState("settings", settingsState);

        //Set everything to render correctly
        controller.setActive("main menu");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}