import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Stack;

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
    @FXML
    private ComboBox<File> imagesCombo;
    @FXML
    private Button saveButton;
    @FXML
    private Button loadButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button pathsButton;
    @FXML
    private Button changeImgButton;
    @FXML
    private Button delImgButton;

    private SpatialMap map;

    private AnchorPane root;
    private Rectangle2D viewport;

    private PointBST pointBST;

    public MapState(StateController stateController) {
        super(stateController);
        pointBST = new PointBST();
    }

    public Region createContent() {
        root = new AnchorPane();

        Pane mainPane = new Pane();
        root.getChildren().add(mainPane);

        //This acts as the default map for now
        //At least until I add a settings/configuration page thing
        map = new SpatialMap("res/maps/map.jpg");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("res/map-menu.fxml"));
            loader.setController(this);
            root.getChildren().add(loader.load());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        //Set the image in the scene as the image stored in the spatial map object.
        Image image = new Image(map.getMapImageFilePath());

        mapImage.setImage(image);
        mapImage.setPreserveRatio(true);
        viewport = new Rectangle2D(0, 0, 500, 500);
        mapImage.setViewport(viewport);

        //This section handles tracking when the user presses the mouse
        //and drags it. It then passes the data of how much it was dragged
        //to the moveMap method.
        //The co-ords also have to be converted, because there is some funky
        //stuff happening with "ImageView space" and "Application Space(?)".
        ObjectProperty<Point2D> mouseDown = new SimpleObjectProperty<>();

        mapImage.setOnMousePressed(e -> {
            Point2D mousePress = convertToMapCoords(mapImage, new Point2D(e.getX(), e.getY()));
            mouseDown.set(mousePress);
        });

        mapImage.setOnMouseDragged(e -> {
            Point2D dragPoint = convertToMapCoords(mapImage, new Point2D(e.getX(), e.getY()));
            moveMap(dragPoint.subtract(mouseDown.get()));
            mouseDown.set(convertToMapCoords(mapImage, new Point2D(e.getX(), e.getY())));
        });

        imagesCombo.setConverter(new StringConverter<File>() {
            @Override
            public String toString(File file) {
                if(file != null)
                    return file.getName().substring(0, file.getName().length() - 4);
                return "";
            }

            @Override
            public File fromString(String s) {
                return null;
            }
        });

        //Add all saved files to the "Load" files combo box.
        addFilesToCombo("src/saves", mapFilesCombo, false);
        //Add all maps to maps combo
        addFilesToCombo("src/res/maps", imagesCombo, true);

        //Disable buttons initially
        saveButton.setDisable(true);
        loadButton.setDisable(true);
        deleteButton.setDisable(true);
        changeImgButton.setDisable(true);
        delImgButton.setDisable(true);

        //Some logic to allow the enabling of some buttons
        mapFilesCombo.setOnAction(e -> {
            if(mapFilesCombo.getValue() != null && !mapFilesCombo.getValue().equals("")){
                loadButton.setDisable(false);
                deleteButton.setDisable(false);
            }
        });

        imagesCombo.setOnAction(e -> {

            if(imagesCombo.getValue() != null){
                changeImgButton.setDisable(false);
                delImgButton.setDisable(false);
            }
        });

        saveNameTF.textProperty().addListener(e -> {
            if(saveNameTF.getText() != null && !saveNameTF.getText().equals("")){
                saveButton.setDisable(false);
            }else{
                saveButton.setDisable(true);
            }
        });

        return root;
    }

    //Convert ImageView co-ords to "map" co-ords.
    //Or rather co-ords on the image itself and not the image view
    private Point2D convertToMapCoords(ImageView iv, Point2D ivCoords) {
        double xRatio = ivCoords.getX() / iv.getBoundsInLocal().getWidth();
        double yRatio = ivCoords.getY() / iv.getBoundsInLocal().getHeight();

        return new Point2D(viewport.getMinX() + xRatio * viewport.getWidth(), viewport.getMinY() + yRatio * viewport.getHeight());
    }

    //Moves the map in accordance with user "drag
    //making it look like the user is dragging the image acorss the scren
    private void moveMap(Point2D change) {
        double width = mapImage.getImage().getWidth();
        double height = mapImage.getImage().getHeight();

        double maxX = width - viewport.getWidth();
        double maxY = height - viewport.getHeight();

        double minX = Math.max(0, Math.min(viewport.getMinX() - change.getX(), maxX));
        double minY = Math.max(0, Math.min(viewport.getMinY() - change.getY(), maxY));

        for(Object o : root.getChildren()){
            if(o instanceof Circle c){
                c.setCenterX(c.getCenterX() + (viewport.getMinX() - minX));
                c.setCenterY(c.getCenterY() + (viewport.getMinY() - minY));
            }
            if(o instanceof Text t){
                t.setX(t.getX() + (viewport.getMinX() - minX));
                t.setY(t.getY() + (viewport.getMinY() - minY));
            }
            if(o instanceof Line l){
                l.setStartX(l.getStartX() + (viewport.getMinX() - minX));
                l.setStartY(l.getStartY() + (viewport.getMinY() - minY));
                l.setEndX(l.getEndX() + (viewport.getMinX() - minX));
                l.setEndY(l.getEndY() + (viewport.getMinY() - minY));
            }
        }

        viewport = new Rectangle2D(minX, minY, viewport.getWidth(), viewport.getHeight());
        mapImage.setViewport(viewport);
    }

    //Add a point to the map, while also making sure that
    //the visual elements of the point are added to the scene.
    private void addPoint(String name, double x, double y) {
        SpatialPoint point = map.addPoint(name, x + viewport.getMinX(), y + viewport.getMinY());
        pointBST.insert(point);

        Circle c = point.getIcon();
        c.setCenterX(c.getCenterX() - viewport.getMinX());
        c.setCenterY(c.getCenterY() - viewport.getMinY());
        Text t = point.getLabel();
        t.setX(t.getX() - viewport.getMinX());
        t.setY(t.getY() - viewport.getMinY());

        root.getChildren().addAll(c, t);
        c.setOnMouseClicked(e -> {
            removePoint(point);
        });
    }

    //Removes point from the map and the visual elements of it
    //from the scene.
    private void removePoint(SpatialPoint p) {
        root.getChildren().removeAll(p.getIcon(), p.getLabel());
        map.removePoint(p);
        pointBST.delete(p);
    }

    //Adds a path to the map and the visual elements of it
    private void addPath(SpatialPath p){
        map.addPath(p);
        Stack<Line> lines = p.getLinesStack();

        for(Line l : lines){
            l.setStartX(l.getStartX() - viewport.getMinX());
            l.setStartY(l.getStartY() - viewport.getMinY());
            l.setEndX(l.getEndX() - viewport.getMinX());
            l.setEndY(l.getEndY() - viewport.getMinY());
            root.getChildren().add(l);
        }
    }

    //Removes a SpatialPath and the visual elements of it
    private void removePath(SpatialPath p){
        Stack<Line> lines = p.getLinesStack();

        while(!lines.isEmpty()){
            root.getChildren().remove(lines.pop());
        }

        map.removePath(p);
    }

    //Add strings representing all the files in the
    //designated save folder to the combo box.
    //The combo box is used for loading those files.
    private void addFilesToCombo(String directory, ComboBox combo, boolean isImageFile) {
        combo.getItems().clear();
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (File f : listOfFiles) {
                if(isImageFile){
                    combo.getItems().add(f);
                }else {
                    String s = f.getName();
                    combo.getItems().add(s.substring(0, s.length() - 4));
                }
            }
        }
    }

    @FXML
    private void addImageButtonAction(){
        //Create file chooser
        FileChooser chooser = new FileChooser();
        //Make it only able to select jpgs
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("JPG Image File", "*.jpg");
        chooser.getExtensionFilters().add(filter);
        //Open it up
        File newMapImage = chooser.showOpenDialog(root.getScene().getWindow());
       if(newMapImage != null){
           //Copy the file into the maps directory
           try {
               Files.copy(newMapImage.toPath(), new File("src/res/maps/" + newMapImage.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
           } catch (IOException e) {
               throw new RuntimeException(e);
           }
       }
       //Add it to the images combo and then refresh, just in case
       //mapFilesCombo.getItems().add(newMapImage.getName().substring(0, newMapImage.getName().length() - 4));
       addFilesToCombo("src/res/maps/", imagesCombo, true);

    }

    //There is some weird stuff going on here.
    //Whenever a file is copied over in the res/maps directory during runtime
    //the program won't recognize it based off of the relative file path.
    //This is only the case when the file is copied over during the current runtime.
    //Best as I can tell it is because relative paths are determined at the start of runtime.
    //To solve this I just get the absolute path of the files when switching
    //However I still save them in relative format. Because upon load I don't want it to be user/computer dependent.
    //It shouldn't be a problem there because no files will be copied when the program starts up.
    @FXML
    private void changeImgButtonAction(){
        map.setMapImageFilePath(imagesCombo.getValue().getPath().replace("src\\", ""));
        try {
            mapImage.setImage(new Image(imagesCombo.getValue().toURI().toURL().toExternalForm()));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void deleteImgButtonAction(){
        imagesCombo.getValue().delete();
        imagesCombo.getItems().remove(imagesCombo.getValue());
        addFilesToCombo("src/res/maps/", imagesCombo, true);
        changeImgButton.setDisable(true);
        delImgButton.setDisable(true);
    }

    //Save the current map.
    //Make sure to add the new file to the combo box.
    @FXML
    private void saveButtonAction() {
        System.out.println("Save Button Pressed: " + saveNameTF.getText());
        map.saveMap(saveNameTF.getText());
        addFilesToCombo("src/saves", mapFilesCombo, false);
    }

    //Load the selected map into the spatial map,
    // and add all the elements to the screen as well.
    @FXML
    private void loadButtonAction() {
        System.out.println("Load Button Pressed: " + mapFilesCombo.getValue());

        for (SpatialPoint p : map.getPoints()) {
            root.getChildren().removeAll(p.getIcon(), p.getLabel());
        }
        for(SpatialPath p : map.getPaths()){
            Stack<Line> lines = p.getLinesStack();
            while(!lines.isEmpty()){
                root.getChildren().remove(lines.pop());
            }
        }
        pointBST.clear();

        map.loadMap(mapFilesCombo.getValue());

        //Change map image
        Image image = new Image(map.getMapImageFilePath());
        mapImage.setImage(image);

        //Add points to screen & bst
        for (SpatialPoint p : map.getPoints()) {
            Circle c = p.getIcon();
            c.setCenterX(c.getCenterX() - viewport.getMinX());
            c.setCenterY(c.getCenterY() - viewport.getMinY());
            c.setOnMouseClicked(e ->{
                removePoint(p);
            });
            Text t = p.getLabel();
            t.setX(t.getX() - viewport.getMinX());
            t.setY(t.getY() - viewport.getMinY());
            root.getChildren().addAll(c, t);

            pointBST.insert(p);
        }

        //Add paths to screen
        for(SpatialPath p : map.getPaths()){
            Stack<Line> lines = p.getLinesStack();
            for(Line l : lines){
                l.setStartX(l.getStartX() - viewport.getMinX());
                l.setStartY(l.getStartY() - viewport.getMinY());
                l.setEndX(l.getEndX() - viewport.getMinX());
                l.setEndY(l.getEndY() - viewport.getMinY());
                root.getChildren().add(l);
            }
        }
    }

    @FXML
    private void deleteButtonAction(){
        new File("src/saves/" + mapFilesCombo.getValue() + ".txt").delete();
        addFilesToCombo("src/saves", mapFilesCombo, false);
        loadButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    //This button activates a pop-up
    //I coded this in a way where I just treat the EventHandler
    //like a standard class
    @FXML
    private void pathsButtonAction(ActionEvent e){
        new EventHandler<ActionEvent>() {
            @FXML
            Button saveButton;
            @FXML
            Button selectButton;
            @FXML
            Button deleteButton;
            @FXML
            Button addButton;
            @FXML
            Button removeButton;
            @FXML
            TextField pathNameTF;
            @FXML
            ComboBox<SpatialPoint> pointsCB;
            @FXML
            ComboBox<SpatialPath> pathsCB;
            @FXML
            TableView<String> pathsTable;

            Stage popoutStage;
            LinkedPointList pathList;

            @Override
            public void handle(ActionEvent actionEvent) {
                popoutStage = new Stage();
                popoutStage.setTitle("Paths");

                AnchorPane root;

                pathList = new LinkedPointList();

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("res/paths-popout.fxml"));
                    loader.setController(this);
                    root = loader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                //Disable buttons
                saveButton.setDisable(true);
                selectButton.setDisable(true);
                deleteButton.setDisable(true);
                addButton.setDisable(true);
                removeButton.setDisable(true);

                //Button enable conditions
                pathNameTF.textProperty().addListener(evt -> {
                    if(pathNameTF.getText() != null && !pathNameTF.getText().equals("")){
                        saveButton.setDisable(false);
                    }else{
                        saveButton.setDisable(true);
                    }
                });

                for (SpatialPoint p : map.getPoints()) {
                    pointsCB.getItems().add(p);
                }
                pointsCB.setConverter(new StringConverter<SpatialPoint>() {
                    @Override
                    public String toString(SpatialPoint spatialPoint) {
                        if(spatialPoint != null)
                            return spatialPoint.getName();
                        return "";
                    }

                    @Override
                    public SpatialPoint fromString(String s) {
                        return null;
                    }
                });
                pointsCB.setOnAction(e -> {
                    if(pointsCB.getValue() != null && !pointsCB.getValue().equals("")){
                        addButton.setDisable(false);
                    }
                });

                for(SpatialPath p : map.getPaths()){
                    pathsCB.getItems().add(p);
                }
                pathsCB.setConverter(new StringConverter<SpatialPath>() {
                    @Override
                    public String toString(SpatialPath spatialPath) {
                        if(spatialPath != null)
                            return spatialPath.getName();
                        return "";
                    }

                    @Override
                    public SpatialPath fromString(String s) {
                        return null;
                    }
                });
                pathsCB.setOnAction(e -> {
                    if(pathsCB.getValue() != null && !pathsCB.getValue().equals("")){
                        deleteButton.setDisable(false);
                        selectButton.setDisable(false);
                    }
                });

                //Set up the table
                pathsTable.getColumns().clear();
                pathsTable.getItems().clear();

                TableColumn<String, String> nameCol = new TableColumn<>("Point Names");
                nameCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()));
                nameCol.setSortable(false);

                pathsTable.getColumns().addAll(nameCol);

                popoutStage.setScene(new Scene(root));
                popoutStage.showAndWait();
            }

            //Adds the selected point to the path
            @FXML
            private void addButtonAction(){
                pathList.addLast(pointsCB.getValue());
                pathsTable.getItems().add(pointsCB.getValue().getName());
                removeButton.setDisable(false);
            }

            //Removes the last added point from the path
            @FXML
            private void removeButtonAction(){
                SpatialPoint p = pathList.removeLast();
                pathsTable.getItems().remove(p.getName());
                if(pathList.getSize() == 0)
                    removeButton.setDisable(true);
            }

            //Saves the path
            @FXML
            private void saveButtonAction(){
                SpatialPath p = new SpatialPath(pathNameTF.getText(), pathList);
                p.generateLines();
                addPath(p);
                pathsCB.getItems().add(p);
            }

            //Deletes the path
            @FXML
            private void deleteButtonAction(){
                removePath(pathsCB.getValue());
                pathsCB.getItems().remove(pathsCB.getValue());
                selectButton.setDisable(true);
                deleteButton.setDisable(true);
            }

            @FXML
            private void selectButtonAction(){
                pathsTable.getItems().clear();
                LinkedPointList newList = new LinkedPointList();
                LinkedPointList.PointNode current = pathsCB.getValue().getPointList().getFirst();
                while(current != null){
                    pathsTable.getItems().add(current.point.getName());
                    newList.addLast(current.point);
                    current = current.next;
                }
                pathList = newList;
            }

        }.handle(e);
    }

    //Add a point when the ImageView object is clicked twice.
    //I made the inner class explicit because it has an FXML doc associated with it,
    //and doing so made working with easier.
    //Spawns a new window that takes the name for the new point.
    @FXML
    private void mapMouseClickedAction(MouseEvent e) {
        if (e.getClickCount() == 2)
            new EventHandler<MouseEvent>() {
                Stage popoutStage;
                @FXML
                TextField pointNameTF;
                @FXML
                Button confirmButton;
                String name = "";

                @Override
                public void handle(MouseEvent e) {
                    popoutStage = new Stage();
                    popoutStage.setTitle("Point Name?");

                    AnchorPane root;

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("res/point-name-popout.fxml"));
                        loader.setController(this);
                        root = loader.load();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    confirmButton.setDisable(true);

                    pointNameTF.textProperty().addListener(evt -> {
                        //the bst use here is lazy. I should go through and alter the code in some way to not make it
                        //awkward like this... but it works and there is other stuff I need to work on.
                        if(pointNameTF.getText() != null && !pointNameTF.getText().equals("") && !pointBST.contains(new SpatialPoint(pointNameTF.getText(), 0, 0))){
                            confirmButton.setDisable(false);
                        }else{
                            confirmButton.setDisable(true);
                        }
                    });

                    popoutStage.setScene(new Scene(root));
                    popoutStage.showAndWait();
                    if(name != null && !name.equals(""))
                        addPoint(name, e.getSceneX(), e.getSceneY());
                }

                @FXML
                private void confirmButtonAction() {
                    //Need to to check something... (TODO)
                    //1. That a name has not been taken by another point
                    popoutStage.close();
                    name = pointNameTF.getText();
                }
            }.handle(e);

    }

    @FXML
    private void mainMenuButtonAction() {
        controller.setActive("main menu");
    }

    //Opens up a new window ("stage") where the user can interact with
    //various stats about the points.
    //For now that is only getting the distances from one point to the others
    //all sorted using merge sort.
    @FXML
    private void statsButtonAction(ActionEvent e) {
        new EventHandler<ActionEvent>() {
            @FXML
            private ComboBox<SpatialPoint> pointsCB;
            @FXML
            private TableView<String[]> pointsTable;
            @FXML
            Button closestButton;

            Stage popoutStage;

            @Override
            public void handle(ActionEvent actionEvent) {
                popoutStage = new Stage();
                popoutStage.setTitle("Stats");

                AnchorPane root;

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("res/stats-popout.fxml"));
                    loader.setController(this);
                    root = loader.load();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                closestButton.setDisable(true);

                pointsCB.setOnAction(e -> {
                    if(pointsCB.getValue() != null && !pointsCB.getValue().equals("")){
                        closestButton.setDisable(false);
                    }
                });

                popoutStage.setScene(new Scene(root));

                for (SpatialPoint p : map.getPoints()) {
                    pointsCB.getItems().add(p);
                }
                pointsCB.setConverter(new StringConverter<SpatialPoint>() {
                    @Override
                    public String toString(SpatialPoint spatialPoint) {
                        if(spatialPoint != null)
                            return spatialPoint.getName();
                        return "";
                    }

                    @Override
                    public SpatialPoint fromString(String s) {
                        return null;
                    }
                });


                popoutStage.showAndWait();
            }

            //Button to activate sorting the points by their distances
            //from a selected point. Uses Merge Sort.
            @FXML
            private void closeButtonAction() {

                pointsTable.getColumns().clear();
                pointsTable.getItems().clear();

                SpatialPoint selectedPoint = pointsCB.getValue();

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

                for (SpatialPoint p : mpArray) {
                    String[] input = {p.getName(), String.valueOf(selectedPoint.distance(p))};
                    pointsTable.getItems().add(input);
                }


            }
        }.handle(e);
    }
}
