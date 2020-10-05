import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;

public class readInput {
    public static Point2D[] readInputFromFile(String filename) {

        String path = "SomeInputs/" + filename;

        In in = new In(path);
        int N = in.readInt();
        Point2D[] points = new Point2D[N];
        for (int i = 0; i < N; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point2D(x, y);
        }
        return points;
    }
}
