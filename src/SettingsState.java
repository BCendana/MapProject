import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This ProgramState represents a screen where users can view, enter, and save settings.
 * For now the settings are only the default map image, and the color of various user defined elements.
 */

public class SettingsState extends ProgramState{

    @FXML
    private ComboBox<String> imagesCombo;
    @FXML
    private Button saveButton;
    @FXML
    private TextField textR, textG, textB;
    @FXML
    private TextField iconR, iconG, iconB;
    @FXML
    private TextField pathR, pathG, pathB;
    @FXML
    private TextField edgeR, edgeG, edgeB;

    private int invalidInputCount;
    private ArrayList<TextField> invalidTFs;

    public SettingsState(StateController controller){
        super(controller);
    }

    public Region createContent(){
        AnchorPane root = new AnchorPane();

        invalidInputCount = 0;
        invalidTFs = new ArrayList<>();

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("res/settings-menu.fxml"));
            loader.setController(this);
            root = loader.load();
        } catch(IOException ex){
            throw new RuntimeException(ex);
        }

        //Add potential image files to combo box
        imagesCombo.getItems().clear();
        File folder = new File("src/res/maps");
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (File f : listOfFiles) {
                imagesCombo.getItems().add(f.getName().substring(0, f.getName().length() - 4));
            }
        }
        //Make the current setting selected in the combo box
        String s = Configuration.getDefaultImageFilepath();
        imagesCombo.setValue(s.substring("res/maps/".length(),s.length()-4));

        Color textColor = Configuration.getTextColor();
        Color iconColor = Configuration.getIconColor();
        Color pathColor = Configuration.getPathColor();
        Color edgeColor = Configuration.getEdgeColor();

        //Set the text in the text fields to their saved/current values
        textR.setText(textColor.getRed() + "");
        textG.setText(textColor.getGreen() + "");
        textB.setText(textColor.getBlue() + "");

        iconR.setText(iconColor.getRed() + "");
        iconG.setText(iconColor.getGreen() + "");
        iconB.setText(iconColor.getBlue() + "");

        pathR.setText(pathColor.getRed() + "");
        pathG.setText(pathColor.getGreen() + "");
        pathB.setText(pathColor.getBlue() + "");

        edgeR.setText(edgeColor.getRed() + "");
        edgeG.setText(edgeColor.getGreen() + "");
        edgeB.setText(edgeColor.getBlue() + "");

        //Upon changing something in any of the text fields,
        //check to make sure that the settings are still valid and should be allowed to save
       EventHandler<ActionEvent> handler = e -> {
           TextField tf = ((TextField) e.getSource());
           String str = tf.getText();
           //if something is invalid increase invalid count and add it to the list
           //if something is valid and is on the bad list decrease invalid count and remove it from the list
           //if invalid count is above zero disable the save button
           if((!isPositiveNumeric(str) || Double.valueOf(str) > 1) && !invalidTFs.contains(tf)){
               invalidTFs.add(tf);
               invalidInputCount++;
           }else if(isPositiveNumeric(str) && Double.valueOf(str) <= 1 && invalidTFs.contains(tf)){
               invalidTFs.remove(tf);
               invalidInputCount--;
           }
           if(invalidInputCount > 0){
               saveButton.setDisable(true);
           }else{
               saveButton.setDisable(false);
           }
       };

        textR.setOnAction(handler);
        textG.setOnAction(handler);
        textB.setOnAction(handler);

        iconR.setOnAction(handler);
        iconG.setOnAction(handler);
        iconB.setOnAction(handler);

        pathR.setOnAction(handler);
        pathG.setOnAction(handler);
        pathB.setOnAction(handler);

        edgeR.setOnAction(handler);
        edgeG.setOnAction(handler);
        edgeB.setOnAction(handler);

        return root;
    }

    //Checks if a string is a positive, potentially decimal, number
    private boolean isPositiveNumeric(String s){
        return s.matches("(\\d+)(\\.\\d+)") || s.matches("\\d+");
    }

    //Saves the settings
    @FXML
    private void saveButtonAction(){

        Color textColor = new Color(Double.valueOf(textR.getText()), Double.valueOf(textG.getText()), Double.valueOf(textB.getText()), 1.0);
        Color iconColor = new Color(Double.valueOf(iconR.getText()), Double.valueOf(iconG.getText()), Double.valueOf(iconB.getText()), 1.0);
        Color pathColor = new Color(Double.valueOf(pathR.getText()), Double.valueOf(pathG.getText()), Double.valueOf(pathB.getText()), 1.0);
        Color edgeColor = new Color(Double.valueOf(edgeR.getText()), Double.valueOf(edgeG.getText()), Double.valueOf(edgeB.getText()), 1.0);

        Configuration.saveConfig(imagesCombo.getValue(), textColor, iconColor, pathColor, edgeColor);
    }

    @FXML
    private void mainMenuButtonAction(){
        controller.setActive("main menu");
    }

}
