/****************************************************************************
 *  Compilation:  javac KdTree.java
 *  Execution:   java KdTree.java
 *  Dependencies: algs4, java Arrays
 *  Author: Eythor Oli Borgthorsson (eythorb19)
 *          Holmfridur Magnea (holmfridurh17)
 *          Sindri Snaer Thorsteinsson (sindrit17)
 *  Date: 05.10.2020
 *
 *  Data structure for maintaining a set of K-D points
 *    in space, including range and nearest-neighbor
 *    queries
 *
 *************************************************************************/


import java.util.ArrayList;
import java.util.Collections;
import edu.princeton.cs.algs4.*;


public class KdTree {

    private Node root;
    private Integer size;

    /**
     * Initializes an empty Kd-Tree.
     */
    public KdTree() {
        size = 0;
    }

    /**
     * Private static class
     */
    private static class Node {
        private Point2D p; // the point
        private RectHV rect; // the axis-aligned rectangle corresponding to this node
        private Node lb; // the left/bottom subtree
        private Node rt; // the right/top subtree
    }

    /**
     * Checks if the tree is empty.
     * @return true if tree empty, else false
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Get number of points in set.
     * @return number of points.
     */
    public int size() {
        return size;
    }

    /**
     * Inserts the specified point into the set, if its not already there
     * @param p 2DPoint
     */
    public void insert(Point2D p) {
        RectHV rect= new RectHV(0.0, 0.0, 1.0, 1.0);
        root = insert(p, root, rect, true);

    };

    // recursive call into tree to find place for the point
    // uses xcmp to check if it the node is vertical or horizontal
    private Node insert(Point2D p, Node n, RectHV rect, boolean xcmp){
        if (n == null){ // root
            return createNode(p, rect);
        }
        if (n.p.equals(p)){ return n; }
        boolean isLeft = isLeft(p, n, xcmp);

        if (xcmp && isLeft) {
            rect = new RectHV(n.rect.xmin(), n.rect.ymin(), n.p.x(), n.rect.ymax());
            n.lb = insert(p,n.lb, rect,!xcmp);

        }
        else if (xcmp && !isLeft){
            rect = new RectHV(n.p.x(), n.rect.ymin(),n.rect.xmax(), n.rect.ymax());
            n.rt = insert(p,n.rt, rect,!xcmp);

        }
        else if (!xcmp && isLeft)
        {
            rect = new RectHV(n.rect.xmin(), n.rect.ymin(), n.rect.xmax(), n.p.y());
            n.lb = insert(p,n.lb, rect,!xcmp);
        }
        else { //(!xcmp && !isLeft)
            rect = new RectHV(n.rect.xmin(), n.p.y(), n.rect.xmax(), n.rect.ymax());
            n.rt = insert(p,n.rt, rect,!xcmp);
        }
        return n; //if removed, java complains about missing return statement
    }

    //creates new node with the in point value and a corresponding rectangle
    private Node createNode (Point2D p, RectHV r) {
        Node newNode = new Node();
        newNode.p = p;
        newNode.rect = r;
        size++;
        return newNode;
    }

    /**
     * Checks if a point is in the set.
     * @param p 2DPoint
     * @return true if set contains point, else false
     */
    public boolean contains(Point2D p) {
        return contains(p, root, true);
    }

    // recursive call into tree to check if it contains the point
    // uses xcmp to check if node to compare is vertical or horizontal
    private boolean contains (Point2D p, Node n, boolean xcmp){
        if (n == null) { return false; }
        if (n.p.equals(p)) { return true; }
        boolean isLeft = isLeft(p, n, xcmp);
        if (isLeft){
            return contains(p, n.lb, !xcmp);
            }
        else {
            return contains(p,n.rt, !xcmp);
        }
    }

    // checks if point is lower or higher than the current node
    // uses xcmp to check if node to compare is vertical or horizontal
    private boolean isLeft (Point2D p, Node n, boolean xcmp){

        return (xcmp ? p.x() < n.p.x() : p.y() < n.p.y());
    }


    /**
     * Draws all points using Standard Draw.
     */
    public void draw() {
        StdDraw.setScale(0, 1);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);

        //  Draw the frame
        root.rect.draw();

        draw(root, root.rect, false);
    }

    // recursive call into tree to print all point and lines.
    private void draw(Node n, RectHV rect, boolean oddLvl) {

        if (n == null) return;

        //  Draw point
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.02);
        n.p.draw();
        StdDraw.setPenRadius();
        if (!oddLvl) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(n.p.x(), n.rect.ymin(), n.p.x(), n.rect.ymax());
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(n.rect.xmin(), n.p.y(), n.rect.xmax(), n.p.y());
        }
        draw(n.lb,n.rect,!oddLvl);
        draw(n.rt, n.rect, !oddLvl);
    }


    /**
     * Returns all the points in the set that are inside the rectangle.
     * @param rect RectHV
     * @return Queue of all 2D points within the rectangle.
     */
    public Iterable<Point2D> range(RectHV rect) {
        Queue<Point2D> inside = new Queue<>();
        return range(rect, inside, root);
    }

    // recursive call into tree to check if node is in rectangle
    // if rectangle does not intersect the node rectangle
    // then no comparison is made and there is no need to dive deeper.
    private Queue<Point2D> range(RectHV r, Queue<Point2D> inside, Node n)
    {
        if ( n==null || !r.intersects(n.rect) ) { return inside; }
        if (r.contains(n.p)){ inside.enqueue(n.p); }
        inside = range(r, inside, n.lb);
        inside = range(r, inside, n.rt);
        return inside;
    }

    /**
     * Finds nearest neighboor.
     * @param p Point2D
     * @return nearest neighboor in the set, returns null if set is empty.
     */
    public Point2D nearest(Point2D p) {
        if (root == null ){return null;}
        return nearest(p, root, root.p, true);
    }

    // recursive call into tree to find the nearest node to point.
    // uses xcmp to check if node is vertical or horizontal and
    // check which site to dive in first depending on placement of point.
    private Point2D nearest(Point2D p, Node n, Point2D near, boolean xcmp){

        if(n == null) { return near; }

        if( near.distanceTo(p) < n.rect.distanceTo(p)) {
            return near;
        }
        if (near.distanceTo(p) > n.p.distanceTo(p)) { near = n.p; }
        boolean isLeft = isLeft(p, n, xcmp);
        if(isLeft){
            near = nearest(p, n.lb, near, !xcmp);
            near = nearest(p, n.rt, near, !xcmp);
        } else {
            near = nearest(p, n.rt, near, !xcmp);
            near = nearest(p, n.lb, near, !xcmp);
        }
        return near;
    }

    /*******************************************************************************
     * Test client
     ******************************************************************************/

    public static void main(String[] args) {
        In in = new In();
        Out out = new Out();
        int N = in.readInt(), R = in.readInt(), T = 800;
        RectHV[] rectangles = new RectHV[R];
        KdTree tree = new KdTree();
        out.printf("Inserting %d points into tree\n", N);

        for (int i = 0; i < N; i++) {
            tree.insert(new Point2D(in.readDouble(), in.readDouble()));
        }

        out.printf("tree.size(): %d\n", tree.size());
        out.printf("Testing `range` method, querying %d rectangles\n", R);
        ArrayList<Point2D> range = new ArrayList<>();
        for (int i = 0; i < R; i++) {
            rectangles[i] = new RectHV(in.readDouble(), in.readDouble(),
                    in.readDouble(), in.readDouble());
            out.printf("Points inside rectangle %s\n", rectangles[i]);
            for (Point2D point : tree.range(rectangles[i])) {
                range.add(point);
            }
            Collections.sort(range);
            for (Point2D point : range) {
                out.printf("%s\n", point);
            }
            range.clear();
        }
        for (int i = 0; i < T; i++) {
            for (int j = 0; j < rectangles.length; j++) {
                tree.range(rectangles[j]);
            }
        }

    }
}
