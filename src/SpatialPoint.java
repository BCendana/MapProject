import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 * This represents a point on a map.
 * Would've named this MapPoint, but am avoid confusing with Map data structures
 */
public class SpatialPoint {
    private double x;
    private double y;

    //A circle for now, will probably be updated to be a custom image in the future.
    private Circle icon;
    private Text text;

    private String name;

    public SpatialPoint(String name, double x, double y){
        this.name = name;
        this.x = x;
        this.y = y;
        icon = new Circle(x, y, 5);
        text = new Text(name);
        //Centering text
        text = new Text(x - text.getLayoutBounds().getWidth()/2, y + 20, name);
    }

    public double distance(SpatialPoint otherPoint){
        double xRes = Math.pow(otherPoint.getX() - x, 2);
        double yRes = Math.pow(otherPoint.getY() - y, 2);
        return Math.sqrt(xRes + yRes);
    }

    //Measuring the complexity of merge sort below.

    public static void mergeSortByDist(SpatialPoint root, SpatialPoint[] points){
        if(points.length > 1){
            //Splits the array in half and take the merge sort of that.
            //Until the length of the array is 1.
            //Will go through log(n) recursions
            SpatialPoint[] firstHalf = new SpatialPoint[points.length / 2];
            System.arraycopy(points, 0, firstHalf, 0, points.length /2);
            mergeSortByDist(root, firstHalf);

            //Splits the array in half and take the merge sort of that.
            //Until the length of the array is 1.
            //Will go through log(n) recursions
            int secondHalfLength = points.length - points.length / 2;
            SpatialPoint[] secondHalf = new SpatialPoint[secondHalfLength];
            System.arraycopy(points, points.length / 2, secondHalf, 0, secondHalfLength);
            mergeSortByDist(root, secondHalf);

            //Merge both halves that have been through their own merge sort process.
            //Takes O(n) time
            mergePointsByDist(root, firstHalf, secondHalf, points);

            //Each step of the process follows:
            //merge(half) + merge(split) + combine

            //Split:
            //Each split splits until the number of elements is 1
            //So n * (1/2) * (1/2) * (1/2)... etc = 1
            //This is the number of times that this recursions
            // n * (1/2)^k = 1;
            // k = log(n)
            // So merge sort has to repeat log(n) times before it is done.
            //The actual splitting of the array takes constant time and is thus ignored.

            //Combine:
            //However, at every recursion you also have to combine the arrays after.
            //Combing the arrays depends on the amount of elements in the arrays, n
            //This is multiplied by a constant for processing this, but constants are ignored.
            //Each next level of recursion needs to combine half the size of the current array twice.
            //So in reality each "level" of recursion is multiplied by n

            //So (log(n) + log(n)) * n;
            //Each half has to go through merge sort log(n) times, with each level multiplied by n
            // So 2log(n) * n
            //Ignore the constants n * log(n) : O(n*log(n))
        }
    }

    public static void mergePointsByDist(SpatialPoint root, SpatialPoint[] sp1, SpatialPoint[] sp2, SpatialPoint[] out){
        int index1 = 0;
        int index2 = 0;
        int indexOut = 0;

        //Throughout this entire function we must get to the "end" of the output array
        //Which is just combining two arrays of n/2 length.
        //So this takes O(n) time.

        while(index1 < sp1.length && index2 < sp2.length){
            if(root.distance(sp1[index1]) <= root.distance(sp2[index2])){
                out[indexOut++] = sp1[index1++];
            }else{
                out[indexOut++] = sp2[index2++];
            }
        }

        while(index1 < sp1.length)
            out[indexOut++] = sp1[index1++];
        while(index2 < sp2.length)
            out[indexOut++] = sp2[index2++];
    }

    public Text getLabel(){
        return text;
    }

    public Circle getIcon(){
        return icon;
    }

    public String getName(){
        return name;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
