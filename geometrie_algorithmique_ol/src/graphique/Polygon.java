package graphique;

import java.util.Vector;

public class Polygon {

    enum Type {
        UNKNOWN, MONOTONE
    }

    /**
     * Index du point le plus haut et le plus bas dans la listes des points
     */
    int topPointIndex, bottomPointIndex;

    /**
     * La liste des points ajouté.
     */
    public Vector<Point> points;

    /**
     * La liste des points affiché.
     */
    public Vector<Point> fusionPoints;

    /**
     * Le tableau des segments affiches.
     */
    public Vector<Segment> segments;

    /**
     * Le tableau des triangulation affiché en fonction de fusion.
     */
    public Vector<Segment> triangulationFusion;

    /**
     * indique si le poligone est monotone
     */
    public Type type;


    Polygon() {
        //
        points = new Vector<Point>();
        segments = new Vector<Segment>();
        //
        fusionPoints = new Vector<Point>();
        triangulationFusion = new Vector<Segment>();
        //
        this.type = Type.UNKNOWN;
    }

    /**
     * Suppression des points et des segments
     */
    public void clear() {
        points.clear();
        segments.clear();
        //
        fusionPoints.clear();
        triangulationFusion.clear();
    }

    public void add(double x, double y) {
        points.add(new Point(x, y));
    }

    public void remove(int num) {
        points.remove(num);
    }

    public Vector<Point> getPoints() { return (this.type== Type.UNKNOWN)? points : fusionPoints;}


    /**
     * La classe Point.
     */
    public static class Point {

        enum Tip {
            NONE("?"), LEFT("L"), RIGHT("R"), TOP("T"), BOTTOM("B");

            private final String str;

            private Tip(String value) {
                this.str = value;
            }

            public String toString() {
                return this.str;
            }
        }

        /**
         * Position dans la le polygone monotone
         */
        public Tip tip;


        /**
         * Numero d'ordre après recherche de l'ordre du polygone
         */
        public int orderOfPolyMonotone;

        /**
         * La valeur de x.
         */
        public double x;

        /**
         * La valeur de y.
         */
        public double y;

        /**
         * Constructeur avec initialisation de x et y.
         */
        public Point(double x, double y) {
            this.x = x;
            this.y = y;
            this.tip = Tip.NONE;
        }

        /**
         * Constructeur sans initialisation.
         */
        public Point() {
        }

        public String toString() {
            return "("+this.x+","+this.y+")";
        }
    }

    /**
     * La classe segment
     */
    public static class Segment {
        /**
         * L'extremite a.
         */
        public Point a;

        /**
         * L'extremite b.
         */
        public Point b;

        /**
         * Constructeur avec initialisation de a et b.
         */
        public Segment(Point a, Point b) {
            this.a = a;
            this.b = b;
        }

        /**
         * Constructeur sans initialisation.
         */
        public Segment() {
        }
    }
}
