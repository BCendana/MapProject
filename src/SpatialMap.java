import javafx.scene.shape.Line;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This represents a map, as in an actual map with places and roads and such.
 * Main features for now is that it contains a list of points on that map
 * as well as a list of paths that connect those points.
 * This class is named SpatialMap in order to try to differentiate from the data structure.
 */
public class SpatialMap {

    //A variable containing the image of the map
    private String mapImageFilePath;
    //An arraylist of "points"
    //private ArrayList<SpatialPoint> points = new ArrayList<>();
    private WeightedPointGraph pointsGraph;
    //An arraylist of paths or "routes"
    private ArrayList<SpatialPath> paths = new ArrayList<>();

    public SpatialMap(String imageFilePath){
        mapImageFilePath = imageFilePath;
        pointsGraph = new WeightedPointGraph();
    }

    public ArrayList<WeightedPointEdge> getNeighbors(SpatialPoint point){
        if(pointsGraph.getPoints().contains(point)){
            return pointsGraph.getNeighbors(point);
        }else{
            return new ArrayList<>();
        }
    }

    public void addEdge(SpatialPoint p1, SpatialPoint p2){
        pointsGraph.addEdge(p1, p2);
    }

    public void addEdge(WeightedPointEdge edge){
        pointsGraph.addEdge(edge);
    }

    public void removeEdge(SpatialPoint p1, SpatialPoint p2){
        pointsGraph.removeEdge(p1, p2);
    }

    public void removeEdge(WeightedPointEdge edge){
        pointsGraph.removeEdge(edge);
    }

    public ArrayList<Line> getGraphLines(){
        return pointsGraph.getLines();
    }

    public SpatialPoint addPoint(String name,double x, double y){
        SpatialPoint p = new SpatialPoint(name, x, y);
        //points.add(p);
        pointsGraph.addPoint(p);
        return p;
    }

    public boolean removePoint(SpatialPoint p){
        //points.remove(p);
        pointsGraph.removePoint(p);
        return true;
    }

    public SpatialPath addPath(SpatialPath p){
        paths.add(p);
        return p;
    }

    public boolean removePath(SpatialPath p){
        paths.remove(p);
        return true;
    }

    public void clear(){
        //points.clear();
        pointsGraph.clear();
        paths.clear();
    }

    //Saves the map into a file
    public void saveMap(String filePath){
        try {
            FileWriter writer = new FileWriter("src/saves/" + filePath + ".txt");

            //The first line is always going to be the file path of the map image
            writer.write(mapImageFilePath + "\n");

            //Format for saving spatial points
            //point,name,x,y
            //for(SpatialPoint p : points){
            for(SpatialPoint p : pointsGraph.getPoints()){
                writer.write("point," + p.getName() + "," + p.getX() + "," + p.getY() + "\n");
            }

            //Here we format this a bit differently
            //Rather than recording all the data of each point again,
            //we instead record its position in the arraylist.
            //This works because in the point saving above the points are recorded in order
            for(SpatialPath p : paths){
                writer.write("path," + p.getName());
                LinkedPointList.PointNode current = p.getPointList().getFirst();
                while(current != null){
                    //writer.write("," + points.indexOf(current.point));
                    writer.write("," + pointsGraph.getPoints().indexOf(current.point));
                    current = current.next;
                }
                writer.write("\n");
            }

            ArrayList<WeightedPointEdge> savedEdges = new ArrayList<>();
            //Add all non-duplicate edges to the save file
            for(SpatialPoint p : pointsGraph.getPoints()){
                for(WeightedPointEdge e : pointsGraph.getNeighbors(p)){
                    //Ensure that only non-duplicates are added
                    //Saves on saves file bloat
                    //and when added back upon loading things will be duplicated
                    //when ensuring that the graph remains undirectional
                    if(!savedEdges.contains(e)){
                        writer.write("edge," + pointsGraph.getPoints().indexOf(e.getP1()) + "," + pointsGraph.getPoints().indexOf(e.getP2()) + "\n");
                        savedEdges.add(e);
                    }
                }
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

            //The first line, aka the image path
            if(scan.hasNextLine()){
                mapImageFilePath = scan.nextLine();
            }

            while(scan.hasNextLine()){
                String s = scan.nextLine();
                String[] parts = s.split(",");
                if(parts[0].equals("point")) {
                    //points.add(new SpatialPoint(parts[1], Double.valueOf(parts[2]), Double.valueOf(parts[3])));
                    pointsGraph.addPoint(new SpatialPoint(parts[1], Double.valueOf(parts[2]), Double.valueOf(parts[3])));
                }else if(parts[0].equals("path")){
                    //Here we are loading points off of our "total" point array
                    //based off of the index that we saved.
                    SpatialPath newPath = new SpatialPath(parts[1]);
                    for(int i = 2; i < parts.length; i++){
                        //newPath.addPoint(points.get(Integer.valueOf(parts[i])));
                        newPath.addPoint(pointsGraph.getPoints().get(Integer.valueOf(parts[i])));
                    }
                    newPath.generateLines();
                    paths.add(newPath);
                }else if(parts[0].equals("edge")){
                    pointsGraph.addEdge(pointsGraph.getPoints().get(Integer.valueOf(parts[1])), pointsGraph.getPoints().get(Integer.valueOf(parts[2])));
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMapImageFilePath(){
        return mapImageFilePath;
    }

    public void setMapImageFilePath(String filePath){
        mapImageFilePath = filePath;
    }

    public ArrayList<SpatialPoint> getPoints(){
        //return points;
        return pointsGraph.getPoints();
    }

    public ArrayList<SpatialPath> getPaths(){
        return paths;
    }
}
