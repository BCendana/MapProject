import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * This class represents a graph data structure. One that is weighted and undirected.
 * Adjacency lists of edges are used instead of matrices. This is because since the amount of
 * edges is user defined, I think having better performance at higher edge counts is better.
 */
public class WeightedPointGraph {

    private ArrayList<SpatialPoint> points;
    //A table of edge lists, with the key being the name of a point the edges are connected to
    private Hashtable<String, ArrayList<WeightedPointEdge>> neighbors;
    //Lines used for rendering
    private ArrayList<Line> lines;

    public WeightedPointGraph(){
        points = new ArrayList<>();
        neighbors = new Hashtable<>();
        lines = new ArrayList<>();
    }

    //Clear all the data from the lists and table
    public void clear(){
        points.clear();
        neighbors.clear();
        lines.clear();
    }

    //Return a list of all the connected points to the given point
    public ArrayList<WeightedPointEdge> getNeighbors(SpatialPoint point){
        return neighbors.get(point.getName());
    }

    public void addEdge(SpatialPoint p1, SpatialPoint p2){
        addEdge(new WeightedPointEdge(p1, p2));
    }

    public void addEdge(WeightedPointEdge edge){
        //To add an edge to the graph both points must exist on it
        if(points.contains(edge.getP1()) && points.contains(edge.getP2())) {
            //To keep the graph undirectional you have to add the edge to both
            //"neighbor" lists of either point.
            neighbors.get(edge.getP1().getName()).add(edge);
            neighbors.get(edge.getP2().getName()).add(edge);
            lines.add(new Line(edge.getP1().getX(), edge.getP1().getY(), edge.getP2().getX(), edge.getP2().getY()));
        }
    }

    public void removeEdge(SpatialPoint p1, SpatialPoint p2){
        removeEdge(new WeightedPointEdge(p1, p2));
    }

    public void removeEdge(WeightedPointEdge edge){
        neighbors.get(edge.getP1().getName()).remove(edge);
        neighbors.get(edge.getP2().getName()).remove(edge);
        //Just try to remove both possible versions of the line. There is a better way to do this I think
        //May improve later
        lines.remove(new Line(edge.getP1().getX(), edge.getP1().getY(), edge.getP2().getX(), edge.getP2().getY()));
        lines.remove(new Line(edge.getP2().getX(), edge.getP2().getY(), edge.getP1().getX(), edge.getP1().getY()));
    }

    public void addPoint(SpatialPoint point){
        if(!points.contains(point)){
            points.add(point);
            //When adding a point to the graph you now have to track its neighbors
            //aka the other points that it has connections to
            //Additionally, points cannot have duplicate names, so using their names as
            //the key for the hashtable should be fine
            neighbors.put(point.getName(), new ArrayList<WeightedPointEdge>());
        }
    }

    public void removePoint(SpatialPoint point){
        points.remove(point);
    }

    public ArrayList<SpatialPoint> getPoints(){
        return points;
    }

    public ArrayList<Line> getLines(){
        return lines;
    }

}
