package at.u4a.geometric_algorithms.geometric;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CloudOfSegments extends Shape {

    final public ArrayList<Segment> cloud = new ArrayList<Segment>();

    public ArrayList<Segment> getOnPosition(Point p) {
        ArrayList<Segment> segments = null;

        for (Segment s : cloud) {
            if (s.contains(p)) {
                if (segments == null)
                    segments = new ArrayList<Segment>();

                segments.add(s);
            }
        }

        return segments;
    }

    public boolean contains(Point p) {
        for (Segment s : cloud) {
            if (s.contains(p))
                return true;
        }

        return false;
    }

    /** Distance entre un point et le segment du nuage le plus proche */
    public double distance(Point p) {
        if (cloud.isEmpty())
            throw new NoSuchElementException("No element in cloud");

        Iterator<Segment> cloudIt = cloud.iterator();

        double minDistance = cloudIt.next().distance(p);

        while (cloudIt.hasNext()) {
            double nextDistance = cloudIt.next().distance(p);
            if (nextDistance < minDistance)
                minDistance = nextDistance;
        }

        return minDistance;
    }

}
