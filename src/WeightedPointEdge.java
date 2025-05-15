/**
 * This class represents an edge, a link between two points in a graph.
 * This is a weighted edge. In the context of the program as a whole,
 * the weight is the distance between two points.
 */
public class WeightedPointEdge {

    private SpatialPoint p1;
    private SpatialPoint p2;
    private double weight;

    public WeightedPointEdge(SpatialPoint p1, SpatialPoint p2){
        this.p1 = p1;
        this.p2 = p2;
        //When adding an edge just makes the weight the distance between the two points
        //There is a future where I don't want it to be this specifically,
        //but it is pretty easy to change if that come to pass
        this.weight = p1.distance(p2);
    }

    public SpatialPoint getP1(){
        return p1;
    }

    public SpatialPoint getP2() {
        return p2;
    }

    public double getWeight(){
        return weight;
    }

    //The equals method works like this because for this program I am implementing
    //the graph as an undirectional graph. Meaning that a edge from a to b is the same thing as
    //an edge from b to a.
    //If the points really are equal, then the weight should be equal as well because it is just the distance.
    @Override
    public boolean equals(Object o){
        return (p1 == ((WeightedPointEdge)o).getP1() && p2 == ((WeightedPointEdge)o).getP2())
                || (p2 == ((WeightedPointEdge)o).getP1() && p1 == ((WeightedPointEdge)o).getP2());
    }
}
