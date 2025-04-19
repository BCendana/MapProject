/**
 * Custom Linked List specifically for storing points.
 * Extremely basic for now, as I just wanted to get it working
 * with the rest of the project.
 * Planned Additions:
 * - Iterator
 * - Nodes store distance to next point
 */

public class LinkedPointList {
    private PointNode head, tail;
    private int size;

    public LinkedPointList(){
    }

    public LinkedPointList(SpatialPoint[] points){
        for(int i = 0; i < points.length; i++){
            add(i, points[i]);
        }
    }

    public void addFirst(SpatialPoint p){
        PointNode newNode = new PointNode(p);
        newNode.next = head;
        head = newNode;
        size++;

        if(tail == null)
            tail = head;
    }

    public void addLast(SpatialPoint p){
        PointNode newNode = new PointNode(p);
        if(tail == null){
            tail = newNode;
            head = newNode;
        }else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    public void add(int index, SpatialPoint p){
        if(index == 0)
            addFirst(p);
        else if(index >= size)
            addLast(p);
        else{
            PointNode current = head;
            for(int i = 1; i < index; i++){
                current = current.next;
            }
            PointNode temp = current.next;
            current.next = new PointNode(p);
            (current.next).next = temp;
            size++;
        }
    }

    public SpatialPoint removeLast(){
        if(size == 0)
            return null;
        else if(size == 1){
            SpatialPoint result = head.point;
            head = null;
            tail = null;
            size = 0;
            return result;
        }else{
            PointNode current = head;

            for(int i = 0; i < size - 2; i++){
                current = current.next;
            }

            SpatialPoint result = tail.point;
            tail = current;
            tail.next = null;
            size--;
            return result;
        }
    }

    public PointNode getFirst(){
        return head;
    }

    public int getSize(){
        return size;
    }

    class PointNode{
        SpatialPoint point;
        PointNode next;

        public PointNode(SpatialPoint point){
            this.point = point;
        }

    }
}
