import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This ProgramState represents the "Map" screen,
 * where the bulk of the program occurs.
 */
public class MapState extends ProgramState {

    @FXML
    private ImageView mapImage;
    @FXML
    private TextField saveNameTF;
    @FXML
    private ComboBox<String> mapFilesCombo;

    private SpatialMap map;
    private AnchorPane root;

    public MapState(StateController stateController) {
        super(stateController);
    }

    public Region createContent() {
        root = new AnchorPane();

        Pane mainPane = new Pane();
        root.getChildren().add(mainPane);

        map = new SpatialMap("res/map.jpg");

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("res/map-menu.fxml"));
            loader.setController(this);
            root.getChildren().add(loader.load());
        } catch(IOException ex){
            throw new RuntimeException(ex);
        }

        //Set the image in the scene as the image stored in the spatial map object.
        mapImage.setImage(new Image(map.getMapImageFilePath()));

        //Add all saved files to the "Load" files combo box.
        addFilesToCombo();

        return root;
    }

    //Add a point to the map, while also making sure that
    //the visual elements of the point are added to the scene.
    private void addPoint(String name, double x, double y) {
        System.out.println(name);
        SpatialPoint point = map.addPoint(name, x, y);
        root.getChildren().addAll(point.getIcon(), point.getLabel());
        point.getIcon().setOnMouseClicked(e -> {
            removePoint(point);
        });
    }

    //Removes point from the map and the visual elements of it
    //from the scene.
    private void removePoint(SpatialPoint p) {
        root.getChildren().removeAll(p.getIcon(), p.getLabel());
        map.removePoint(p);
    }

    //Add strings representing all the files in the
    //designated save folder to the combo box.
    //The combo box is used for loading those files.
    private void addFilesToCombo(){
        mapFilesCombo.getItems().clear();
        File folder = new File("src/saves");
        File[] listOfFiles = folder.listFiles();

        if(listOfFiles != null){
            for(int i = 0; i < listOfFiles.length; i++){
                String s = listOfFiles[i].getName();
                mapFilesCombo.getItems().add(s.substring(0, s.length()-4));
            }
        }
    }

    //Save the current map.
    //Make sure to add the new file to the combo box.
    @FXML
    private void saveButtonAction(){
        System.out.println("Save Button Pressed: " + saveNameTF.getText());
        //Prob move the little string snippets to the actual method
        map.saveMap(saveNameTF.getText());
        addFilesToCombo();
    }

    //Load the selected map into the spatial map,
    // and add all the elements to the screen as well.
    @FXML
    private void loadButtonAction(){
        System.out.println("Load Button Pressed: " + mapFilesCombo.getValue());

        for(SpatialPoint p : map.getPoints()){
            root.getChildren().removeAll(p.getIcon(), p.getLabel());
        }

        map.loadMap(mapFilesCombo.getValue());

        for(SpatialPoint p : map.getPoints()){
            root.getChildren().addAll(p.getIcon(), p.getLabel());
        }
    }

    //Add a point when the ImageView object is clicked.
    //I made the inner class explicit because it has an FXML doc associated with it,
    //and doing so made working with easier.
    //Spawns a new window that takes the name for the new point.
    @FXML
    private void mapMouseClickedAction(MouseEvent e){
        new EventHandler<MouseEvent>() {
            Stage popoutStage;
            @FXML
            TextField pointNameTF;
            String name = "";

            @Override
            public void handle(MouseEvent e) {
                popoutStage = new Stage();
                popoutStage.setTitle("Point Name?");

                AnchorPane root = new AnchorPane();

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("res/point-name-popout.fxml"));
                    loader.setController(this);
                    root = loader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                popoutStage.setScene(new Scene(root));
                popoutStage.showAndWait();

                addPoint(name, e.getSceneX(), e.getSceneY());
            }

            @FXML
            private void confirmButtonAction(){
                //Need to to check two things... (TODO)
                //1. That a name has actually been entered
                //2. That a name has not been taken by another point
                popoutStage.close();
                name = pointNameTF.getText();
            }
        }.handle(e);

    }

    @FXML
    private void mainMenuButtonAction(){
        controller.setActive("main menu");
    }

    //Opens up a new window ("stage") where the user can interact with
    //various stats about the points.
    //For now that is only getting the distances from one point to the others
    //all sorted using merge sort.
    @FXML
    private void statsButtonAction(ActionEvent e){
        new EventHandler<ActionEvent>() {
            @FXML
            private ComboBox<String> pointsCB;
            @FXML
            private TableView<String[]> pointsTable;

            Stage popoutStage;

            @Override
            public void handle(ActionEvent actionEvent) {
                popoutStage = new Stage();
                popoutStage.setTitle("Stats");

                AnchorPane root = new AnchorPane();

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("res/stats-popout.fxml"));
                    loader.setController(this);
                    root = loader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                popoutStage.setScene(new Scene(root));

                for(SpatialPoint p : map.getPoints()){
                    pointsCB.getItems().add(p.getName());
                }

                popoutStage.showAndWait();
            }

            //Button to activate sorting the points by their distances
            //from a selected point. Uses Merge Sort.
            @FXML
            private void closeButtonAction(){

                //This is inefficient
                //Later I will go through and make it so that the combo box stores
                //the Points themselves rather than just the name of it.
                SpatialPoint selectedPoint = map.getPoints().get(0);
                for(SpatialPoint p : map.getPoints()){
                    if(p.getName().equals(pointsCB.getValue())) {
                        selectedPoint = p;
                        break;
                    }
                }

                ArrayList<SpatialPoint> measurePoints = (ArrayList<SpatialPoint>) map.getPoints().clone();
                measurePoints.remove(selectedPoint);

                SpatialPoint[] mpArray = measurePoints.toArray(new SpatialPoint[measurePoints.size()]);

                SpatialPoint.mergeSortByDist(selectedPoint, mpArray);

                TableColumn<String[], String> nameCol = new TableColumn<>("Point Names");
                nameCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[0]));
                nameCol.setSortable(false);
                TableColumn<String[], String> distCol = new TableColumn<>("Distances");
                distCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[1]));
                distCol.setSortable(false);

                pointsTable.getColumns().addAll(nameCol, distCol);

                for(SpatialPoint p : mpArray){
                    String[] input = {p.getName(), String.valueOf(selectedPoint.distance(p))};
                    pointsTable.getItems().add(input);
                }


            }
        }.handle(e);
    }
}
