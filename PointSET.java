
/****************************************************************************
 *  Compilation:  javac PointSET.java
 *  Execution:   java PointSET.java
 *  Dependencies: algs4, java Arrays
 *  Author: Holmfridur Magnea (holmfridurh17)
 *          Sindri Snaer Thorsteinsson (sindrit17)
 *          Eythor Oli Borgthorsson (eythorb19)
 *  Date: 05.10.2020
 *
 *  Data structure for maintaining a set of 2-D points, 
 *    including rectangle and nearest-neighbor queries
 *
 *************************************************************************/

import java.util.Arrays;
import edu.princeton.cs.algs4.*;


public class PointSET {
    // construct an empty set of points
    private RedBlackBST<Point2D, String> tree;

    public PointSET() {
        tree = new RedBlackBST<>();
    }

    /**
     * Checks if the tree is empty.
     * @return true if tree empty, else false
     */
    public boolean isEmpty() {
        return tree.isEmpty();
    }

    /**
     * Get number of points in set.
     * @return number of points.
     */
    public int size() {
        return tree.size();
    }

    /**
     * Inserts the specified point into the set, if its not already there
     * @param p 2DPoint
     */
    public void insert(Point2D p) {
        tree.put(p, "point");   //  Overwrites the old value if the set contains the point
    }

    /**
     * Checks if a point is in the set.
     * @param p 2DPoint
     * @return true if set contains point, else false
     */
    public boolean contains(Point2D p) {
        return tree.contains(p);
    }

    /**
     * Draws all points using Standard Draw.
     */
    public void draw() {
        Iterable<Point2D> points = tree.keys();
        points.forEach(point2D -> draw());
    }

    /**
     * Returns all the points in the set that are inside the rectangle.
     * @param rect RectHV
     * @return Queue of all 2D points within the rectangle.
     */
    public Iterable<Point2D> range(RectHV rect) {
        //  All keys
        Iterable<Point2D> points = tree.keys();
        //  create new queue to return iterable
        Queue<Point2D> inside = new Queue<>();
        points.forEach(point2D -> {
            if (rect.contains(point2D))
            {
                inside.enqueue(point2D);
            }
        });
        return inside;
    }

    /**
     * Finds nearest neighboor.
     * @param p Point2D
     * @return nearest neighboor in the set, returns 0 if set is empty.
     */
    public Point2D nearest(Point2D p) {
        Point2D nearest = tree.min();
        //  for every key in the points of the tree check if the distance is shorter and not same point
        for (Point2D point : tree.keys()){
            if ((p.distanceSquaredTo(point) < p.distanceSquaredTo(nearest)) && !(p.equals(point))) {
                nearest = point;
            }
        }
        return nearest;
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
        PointSET set = new PointSET();
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
