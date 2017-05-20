package at.u4a.geometric_algorithms.geometric;

import at.u4a.geometric_algorithms.graphic_visitor.InterfaceGeometricPainterVisitor;

public class Line implements InterfaceGeometric {

    /* HELPER CLASS */

    public class Inclinaison {

        public final double gradiant;
        public final double decal;
        public final boolean isInfinite;
        public final boolean compute;

        public Inclinaison() {

            final double xDecal = b.x - a.x;
            final double yDecal = b.y - a.y;

            if ((xDecal == 0) && (yDecal == 0)) {
                gradiant = 0;
                decal = 0;
                isInfinite = false;
                compute = false;
                return;
            }

            if (xDecal == 0) {
                gradiant = 0;
                isInfinite = true;
                decal = a.x;
            } else {
                gradiant = yDecal / xDecal;
                decal = a.y - gradiant * a.x;
                isInfinite = false;
            }
            compute = true;
        }

        public boolean equals(InclinaisonHV other) {
            return this.gradiant == other.gradiant && this.decal == other.decal;
        }
    }

    /** @todo in construction for more precision, see Calc.intersectionHV */
    public class InclinaisonHV {

        public final double gradiant;
        public final double decal;
        public final boolean isHorizontal;
        public final boolean compute;

        public final double xDecal;
        public final double yDecal;

        public InclinaisonHV() {

            xDecal = b.x - a.x;
            yDecal = b.y - a.y;

            if ((xDecal == 0) && (yDecal == 0)) {
                gradiant = 0;
                decal = 0;
                isHorizontal = false;
                compute = false;
                return;
            }

            if (Math.abs(xDecal) >= Math.abs(yDecal)) {
                isHorizontal = true;
                gradiant = yDecal / xDecal;
                decal = a.y - gradiant * a.x;
            } else {
                isHorizontal = false;
                gradiant = xDecal / yDecal;
                decal = a.x - gradiant * a.y;
            }

            compute = true;

        }

        public boolean equals(InclinaisonHV other) {
            return this.gradiant == other.gradiant && this.isHorizontal == other.isHorizontal && this.decal == other.decal;
        }
    }

    /* */

    private final static double EPSILON = 1;

    public final Point a, b;

    public Line(Point a, Point b) {
        this.a = a;
        this.b = b;
    }

    public Line() {
        a = new Point();
        b = new Point();
    }

    public boolean equals(Line other) {
        return this.a.equals(other.a) && this.b.equals(other.b);
    }

    public String toString() {
        return "(" + a.toString() + ";" + b.toString() + ")";
    }

    public int hashCode() {
        return a.hashCode() * 31 + b.hashCode();
    }

    public void set(Line other) {
        this.a.set(other.a);
        this.b.set(other.b);
    }

    public void translate(Point p) {
        a.translate(p);
        b.translate(p);
    }

    public boolean contains(Point p) {
        return (Math.abs(distance(p)) <= EPSILON) || a.contains(p) || b.contains(p);
    }

    @Override
    public InterfaceGeometric getContains(Point p) {
        return contains(p) ? this : null;
    }

    /** Formule de distance récuperé dans le Partiel 2016/2017 de PCOO */
    public double distance(Point p) {
        return ((b.x - a.x) * (a.y - p.y) - (a.x - p.x) * (b.y - a.y)) / Math.hypot(b.x - a.x, b.y - a.y);
    }

    @Override
    public void accept(InterfaceGeometricPainterVisitor visitor) {
        visitor.visit(this);
    }

    public final Inclinaison getInclinaison() {
        return new Inclinaison();
    }

    public final InclinaisonHV getInclinaisonHV() {
        return new InclinaisonHV();
    }

}
