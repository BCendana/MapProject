import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * This class simply holds all the program's settings as static variables.
 * These values can be loaded from and saved to a file for consistent data across sessions.
 * Currently, the settings are only changed from the SettingsState.
 */
public class Configuration {

    private static final String CONFIG_FILE_PATH = "src/config/configuration.txt";

    private static String defaultImageFilepath;
    private static Color textColor, iconColor, pathColor, edgeColor;

    //Load the variable stored in the configuration.txt file into the program
    public static void loadConfig(){
        try {
            File configFile = new File(CONFIG_FILE_PATH);
            Scanner scan = new Scanner(configFile);

            //Check what the lines start with to determine what data they hold
            while(scan.hasNextLine()){
                String s = scan.nextLine();
                if(s.startsWith("defaultImageFilepath=")){
                    defaultImageFilepath = "res/maps/" + s.substring(s.indexOf('=')+1) + ".jpg";
                }else if(s.startsWith("textColor=")){
                    String[] parts = s.substring("textColor=".length()).split(",");
                    textColor = new Color(Double.valueOf(parts[0]), Double.valueOf(parts[1]), Double.valueOf(parts[2]), 1.0);
                }else if(s.startsWith("iconColor=")){
                    String[] parts = s.substring("textColor=".length()).split(",");
                    iconColor = new Color(Double.valueOf(parts[0]), Double.valueOf(parts[1]), Double.valueOf(parts[2]), 1.0);
                }else if(s.startsWith("pathColor=")){
                    String[] parts = s.substring("textColor=".length()).split(",");
                    pathColor = new Color(Double.valueOf(parts[0]), Double.valueOf(parts[1]), Double.valueOf(parts[2]), 1.0);
                }else if(s.startsWith("edgeColor=")){
                    String[] parts = s.substring("textColor=".length()).split(",");
                    edgeColor = new Color(Double.valueOf(parts[0]), Double.valueOf(parts[1]), Double.valueOf(parts[2]), 1.0);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //Write the config to the configuration.txt file
    public static void saveConfig(String defaultImageFilepath, Color textColor, Color iconColor, Color pathColor, Color edgeColor){
        try {
            FileWriter writer = new FileWriter("src/config/configuration.txt");

            //Simply write the data with a simple prefix signifying what it is.
            writer.write("defaultImageFilepath=" + defaultImageFilepath + "\n");
            writer.write("textColor=" + textColor.getRed() + "," + textColor.getGreen() + "," + textColor.getBlue() + "\n");
            writer.write("iconColor=" + iconColor.getRed() + "," + iconColor.getGreen() + "," + iconColor.getBlue() + "\n");
            writer.write("pathColor=" + pathColor.getRed() + "," + pathColor.getGreen() + "," + pathColor.getBlue() + "\n");
            writer.write("edgeColor=" + edgeColor.getRed() + "," + edgeColor.getGreen() + "," + edgeColor.getBlue() + "\n");

            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        loadConfig();
    }

    public static String getDefaultImageFilepath(){
        return defaultImageFilepath;
    }

    public static Color getTextColor(){
        return textColor;
    }

    public static Color getIconColor(){
        return iconColor;
    }

    public static Color getPathColor(){
        return pathColor;
    }

    public static Color getEdgeColor(){
        return edgeColor;
    }

}
