package at.u4a.geometric_algorithms.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import at.u4a.geometric_algorithms.geometric.Point;

public class NonOrientedGraph<T> {

    public class Vertice {
        public final HashSet<Vertice> joins = new HashSet<Vertice>();
        public final T t;

        public Vertice(T t) {
            this.t = t;
        }
    }

    /*
     * public static class Edge { public Vertice back = null, next = null;
     * 
     * Edge(Vertice back, Vertice next) { this.back = back; this.next = next; }
     * }
     */

    public HashMap<T, Vertice> vertices = new HashMap<T, Vertice>();
    // private Vector<Edge> edges = new Vector<Edge>();

    public void addArc(Vertice a, Vertice b) {
        // vertices.get(a);
        a.joins.add(b);
        b.joins.add(a);
    }

    public void clear() {
        vertices.clear();
        // edges.clear();
    }

    public void addVerticle(T p) {
        vertices.put(p, new Vertice(p));
    }

}
