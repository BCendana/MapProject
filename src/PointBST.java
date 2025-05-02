/**
 * This class is a binary search tree for points
 * There is nothing special about it aside from the fact that its contents are
 * specifically SpatialPoints and are sorted and found based off of their names.
 *
 * It also assumes that there are no duplicate names.
 */
public class PointBST {

    PointNode root;

    public PointBST(){
        root = null;
    }

    public void clear(){
        root = null;
    }

    public boolean contains(SpatialPoint point){
        PointNode current = root;

        while(current != null){
            if(point.getName().compareTo(current.point.getName()) < 0){
                current = current.left;
            }else if(point.getName().compareTo(current.point.getName()) > 0){
                current = current.right;
            }
            else{
                return true;
            }
        }

        return false;
    }

    public void insert(SpatialPoint point){
        root = insertAt(root, point);
    }

    private PointNode insertAt(PointNode node, SpatialPoint point){
        //if (sub)tree is empty make inserted point the root node (of this subtree)
        if(node == null){
            node = new PointNode(point);
            return node;
        }

        //if alphabetically less pass it down the left, if greater than pass it down the right
        if(point.getName().compareTo(node.point.getName()) < 0){
            node.left = insertAt(node.left, point);
        }else if(point.getName().compareTo(node.point.getName()) > 0){
            node.right = insertAt(node.right, point);
        }

        return node;
    }

    public void delete(SpatialPoint point){
        PointNode parent = null;
        PointNode current = root;

        while(current != null){
            if(point.getName().compareTo(current.point.getName()) < 0){
                parent = current;
                current = current.left;
            }else if(point.getName().compareTo(current.point.getName()) > 0){
                parent = current;
                current = current.right;
            }
            else{
                break;
            }
        }

        //If current is still null, then the point is not in the tree
        if(current == null){
            return;
        }

        //if the left is empty, then just attach the right child up
        if(current.left == null){
            if(parent == null){
                root = current.right;
            }else{
                if(point.getName().compareTo(current.point.getName()) < 0){
                    parent.left = current.right;
                }else if(point.getName().compareTo(current.point.getName()) > 0){
                    parent.right = current.right;
                }
            }
        } else{
            PointNode parentOfRightMost = current;
            PointNode rightMost = current.left;

            //Look for the rightmost node and parent of it
            while(rightMost.right != null){
                parentOfRightMost = rightMost;
                rightMost = rightMost.right;
            }

            //replace current with it
            current.point = rightMost.point;

            //and then delete rightmost
            if(parentOfRightMost.right == rightMost)
                parentOfRightMost.right = rightMost.left;
            else
                parentOfRightMost.left = rightMost.left;
        }
    }


    class PointNode{
        SpatialPoint point;
        PointNode left;
        PointNode right;

        public PointNode(SpatialPoint point){
            this.point = point;
            left = null;
            right = null;
        }
    }

}
