
/*************************************************************************
 *************************************************************************/

import java.util.Arrays;

import edu.princeton.cs.algs4.*;

public class KdTree {

    private Node root;
    private Integer size;


    private final String X = "x";
    private final String Y = "y";
    /**
     * Initializes an empty symbol table.
     */
    public KdTree() {
        size = 0;
    }

    private static class Node {
        private Point2D p; // the point
        private RectHV rect; // the axis-aligned rectangle corresponding to this node
        private Node lb; // the left/bottom subtree
        private Node rt; // the right/top subtree
        //private String lvl;
    }

    // is the set empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // number of points in the set
    public int size() {
        return size();
    }

    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p) {
            root = insert(p, root, X);

    };

    private Node insert(Point2D p, Node n,String lvl){
        if (n == null){
            RectHV rect = new RectHV(0.0, 0.0, 1.0, 1.0);
            return createNode(p, rect);
        }
        if (lvl == X){
            if (p.x() < n.p.x()) {
                if(n.lb != null) {n.lb = insert(p, n.lb, Y);}
                else {
                    RectHV rect = new RectHV(n.rect.xmin(), n.rect.ymin(), n.rect.xmax(), p.y());
                    n.lb = createNode(p, rect);
                    return n;
                }
            }
            else { // if(p.x() >= n.p.x())
                if (n.rt != null) { n.rt = insert(p, n.rt, Y); }
                else {
                    RectHV rect = new RectHV(n.rect.xmin(), p.y(), n.rect.xmax(), n.rect.ymax());
                    n.rt = createNode(p, rect);
                    return n;
                }
                }
        }
        else {  //if (lvl == Y)
            if (p.y() < n.p.y()) {
                if (n.lb != null ) { n.lb = insert(p, n.lb, X); }
                else {
                    RectHV rect = new RectHV(n.rect.xmin(), n.rect.ymin(), p.x(), n.rect.ymax());
                    n.lb = createNode(p, rect);
                    return n;
                }
            }
            else { // if(p.y() >= n.p.y())
                if ( n.rt != null ) { n.rt = insert(p, n.rt, X); }
                else {
                    RectHV rect = new RectHV(p.x(), n.rect.ymin(), n.rect.xmax(), n.rect.ymax());
                    n.rt = createNode(p, rect);
                    return n;
                }
            }
        }
        return n; //if removed, java complains about missing return statement
    }

    private Node createNode (Point2D p, RectHV r) {
        Node newNode = new Node();
        newNode.p = p;
        newNode.rect = r;
        size++;
        return newNode;
    }

    // does the set contain the point p?
    public boolean contains(Point2D p) {
        return true;
        //return tree.contains(p);
    }

    // draw all of the points to standard draw
    public void draw() {

    }

    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        Queue<Point2D> inside = new Queue<>();
        return range(rect, inside, root);
    }

    private Queue<Point2D> range(RectHV r, Queue<Point2D> inside, Node n)
    {
        if ( n==null ) { return inside; } // veit ekki með return inside
        if (r.intersects(n.rect)) {
            if (r.contains(n.p)){ inside.enqueue(n.p); }
            inside = range(r, inside, n.lb);
            inside = range(r, inside, n.rt);
        }
        return inside;
    }

    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p) {
        //Nearest neighbor search. To find a closest point to a given query point, start at the root and recursively
        // search in both subtrees using the following pruning rule:
        // if the closest point discovered so far is closer than the distance between the query point and the rectangle
        // corresponding to a node, there is no need to explore that node (or its subtrees).
        // That is, a node is searched only if it might contain a point that is closer than the best one found so far.
        // The effectiveness of the pruning rule depends on quickly finding a nearby point. To do this, organize your
        // recursive method so that when there are two possible subtrees to go down, you always choose the subtree that
        // is on the same side of the splitting line as the query point as the first subtree to explore—the closest
        // point found while exploring the first subtree may enable pruning of the second subtree.
        return nearest(p, root, root.p);
    }

    private Point2D nearest(Point2D p, Node n, Point2D near){

        if(n == null) { return near; }
        if (near.distanceTo(p) < n.p.distanceTo(p) && near.distanceTo(p) < n.rect.distanceTo(p)){
            return near;
        }
        if (near.distanceTo(p) > n.p.distanceTo(p)) { near = n.p; }
        // TODO: virkar ekki þvi lb og rt getur verið null !!
        if (n.lb.rect.contains(p)){
            if (n.lb.rect.distanceTo(p) < near.distanceTo(p)) { near = nearest(p, n.lb, near); }
            if (n.rt.rect.distanceTo(p) < near.distanceTo(p)) { near = nearest(p, n.rt, near); }
        }

        else { //(n.rt.rect.contains(p))
            if (n.rt.rect.distanceTo(p) < near.distanceTo(p)) { near = nearest(p, n.rt, near); }
            if (n.lb.rect.distanceTo(p) < near.distanceTo(p)) { near = nearest(p, n.lb, near); }
        }

        return near;
    }

    /*******************************************************************************
     * Test client
     ******************************************************************************/
    public static void main(String[] args) {
        In in = new In();
        Out out = new Out();
        int nrOfRecangles = in.readInt();
        int nrOfPointsCont = in.readInt();
        int nrOfPointsNear = in.readInt();
        RectHV[] rectangles = new RectHV[nrOfRecangles];
        Point2D[] pointsCont = new Point2D[nrOfPointsCont];
        Point2D[] pointsNear = new Point2D[nrOfPointsNear];
        for (int i = 0; i < nrOfRecangles; i++) {
            rectangles[i] = new RectHV(in.readDouble(), in.readDouble(),
                    in.readDouble(), in.readDouble());
        }
        for (int i = 0; i < nrOfPointsCont; i++) {
            pointsCont[i] = new Point2D(in.readDouble(), in.readDouble());
        }
        for (int i = 0; i < nrOfPointsNear; i++) {
            pointsNear[i] = new Point2D(in.readDouble(), in.readDouble());
        }
        KdTree set = new KdTree();
        for (int i = 0; !in.isEmpty(); i++) {
            double x = in.readDouble(), y = in.readDouble();
            set.insert(new Point2D(x, y));
        }
        for (int i = 0; i < nrOfRecangles; i++) {
            // Query on rectangle i, sort the result, and print
            Iterable<Point2D> ptset = set.range(rectangles[i]);
            int ptcount = 0;
            for (Point2D p : ptset)
                ptcount++;
            Point2D[] ptarr = new Point2D[ptcount];
            int j = 0;
            for (Point2D p : ptset) {
                ptarr[j] = p;
                j++;
            }
            Arrays.sort(ptarr);
            out.println("Inside rectangle " + (i + 1) + ":");
            for (j = 0; j < ptcount; j++)
                out.println(ptarr[j]);
        }
        out.println("Contain test:");
        for (int i = 0; i < nrOfPointsCont; i++) {
            out.println((i + 1) + ": " + set.contains(pointsCont[i]));
        }

        out.println("Nearest test:");
        for (int i = 0; i < nrOfPointsNear; i++) {
            out.println((i + 1) + ": " + set.nearest(pointsNear[i]));
        }

        out.println();
    }
}
