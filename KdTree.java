
/*************************************************************************
 *************************************************************************/

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import edu.princeton.cs.algs4.*;
import org.w3c.dom.css.Rect;

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
        private String dir;
        //private String lvl;
    }

    // is the set empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p) {
        RectHV rect= new RectHV(0.0, 0.0, 1.0, 1.0);
            root = insert(p, root, rect, true);

    };
    /*
    *
    * @param
    *
    *
     */

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

    private Node createNode (Point2D p, RectHV r) {
        Node newNode = new Node();
        newNode.p = p;
        newNode.rect = r;
        size++;
        return newNode;
    }

    // does the set contain the point p?
    public boolean contains(Point2D p) {
        return contains(p, root, true);
    }

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

    private boolean isLeft (Point2D p, Node n, boolean xcmp){

        return (xcmp ? p.x() < n.p.x() : p.y() < n.p.y());
    }

    // draw all of the points to standard draw
    public void draw() {
        StdDraw.setScale(0, 1);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.02);

        //  Draw the frame
        root.rect.draw();

        draw(root, root.rect, false);
    }

    private void draw(Node n, RectHV rect, boolean oddLvl) {

        if (n == null) return;

        //  Draw
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        n.p.draw();

        Point2D min, max;
        //  Find min and max of the corresponding line
        if (!oddLvl) {
            StdDraw.setPenColor(StdDraw.RED);
            min = new Point2D(n.p.x(), rect.ymin());
            max = new Point2D(n.p.x(), rect.ymax());
        }
        else {
            StdDraw.setPenColor(StdDraw.BLACK);
            min = new Point2D(rect.xmin(), n.p.y());
            max = new Point2D(rect.xmax(), n.p.y());
        }

        //  Draw corresponding line
        StdDraw.setPenRadius();
        min.drawTo(max);

        draw(n.lb, drawRect(n, rect, oddLvl), oddLvl);
        draw(n.rt, drawRect(n, rect, oddLvl), !oddLvl);
    }

    private RectHV drawRect(Node n, RectHV rect, boolean oddLvl) {

        if (!oddLvl) {
            return new RectHV(rect.xmin(), rect.ymin(), n.p.x(), rect.ymax());
        }
        else {
            return new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), n.p.y());
        }

    }


    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        Queue<Point2D> inside = new Queue<>();
        return range(rect, inside, root);
    }

    private Queue<Point2D> range(RectHV r, Queue<Point2D> inside, Node n)
    {
        if ( n==null || !r.intersects(n.rect) ) { return inside; }
        //if (!r.intersects(n.rect)) { return inside; }// veit ekki med return inside
        if (r.contains(n.p)){ inside.enqueue(n.p); }
        inside = range(r, inside, n.lb);
        inside = range(r, inside, n.rt);

        return inside;
    }

    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p) {
        // TODO: root == null ?
        return nearest(p, root, root.p, true);
    }

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

        tree.draw();
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
