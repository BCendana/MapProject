import javafx.scene.shape.Line;

import java.util.Stack;

/**
 * This class holds a custom linked list of points to represent
 * a user defined path. From point to point. Each point only points towards
 * the next point in the path. (aka a linked list)
 */
public class SpatialPath {
    private String name;
    private LinkedPointList pointList;
    private Stack<Line> lines;

    public SpatialPath(String name){
        this.name = name;
        pointList = new LinkedPointList();
        lines = new Stack<>();
    }

    public SpatialPath(String name, SpatialPoint[] points){
        this.name = name;
        pointList = new LinkedPointList(points);
        lines = new Stack<>();
    }

    public SpatialPath(String name, LinkedPointList pointList){
        this.name = name;
        this.pointList = pointList;
        lines = new Stack<>();
    }

    public void generateLines(){
        LinkedPointList.PointNode current = pointList.getFirst();
        while(current.next != null){
            Line l = new Line(current.point.getX(), current.point.getY(), current.next.point.getX(), current.next.point.getY());
            lines.push(l);
            current = current.next;
        }
    }

    public void addPoint(SpatialPoint p){
        pointList.addLast(p);
    }

    public LinkedPointList getPointList(){
        return pointList;
    }

    public Stack getLinesStack(){
        return lines;
    }

    public String getName(){
        return name;
    }

}
