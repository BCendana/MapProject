import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This represents a map, as in an actual map with places and roads and such.
 * Main feature for now is that it contains a list of points on that map.
 * This class is named SpatialMap in order to try to differentiate from the data structure.
 */
public class SpatialMap {

    //A variable containing the image of the map
    private String mapImageFilePath;
    //An arraylist of "points"
    private ArrayList<SpatialPoint> points = new ArrayList<>();

    public SpatialMap(String imageFilePath){
        mapImageFilePath = imageFilePath;
    }

    public SpatialPoint addPoint(String name,double x, double y){
        SpatialPoint p = new SpatialPoint(name, x, y);
        points.add(p);
        return p;
    }

    public boolean removePoint(SpatialPoint p){
        points.remove(p);
        return true;
    }

    public void clear(){
        points.clear();
    }

    //Saves the map into a file
    public void saveMap(String filePath){
        try {
            FileWriter writer = new FileWriter("src/saves/" + filePath + ".txt");

            //Format for saving spatial points
            //name,x,y
            for(SpatialPoint p : points){
                writer.write(p.getName() + "," + p.getX() + "," + p.getY() + "\n");
            }

            writer.close();

        } catch (IOException e) {
            System.out.println("Error saving Spatial Map file");
            throw new RuntimeException(e);
        }
    }

    //Loads map from file.
    //Clears current data and adds the translated data from the file.
    public void loadMap(String filePath){
        clear();
        File mapFile = new File("src/saves/" + filePath + ".txt");
        try {
            Scanner scan = new Scanner(mapFile);

            while(scan.hasNextLine()){
                String s = scan.nextLine();
                String[] parts = s.split(",");
                points.add(new SpatialPoint(parts[0], Double.valueOf(parts[1]), Double.valueOf(parts[2])));
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMapImageFilePath(){
        return mapImageFilePath;
    }

    public ArrayList<SpatialPoint> getPoints(){
        return points;
    }
}
