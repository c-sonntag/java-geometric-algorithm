package at.u4a.geometric_algorithms.gui.layer;

public enum LayerCategory {

    Geometry("G", "Geometry"), Algorithm("A", "Algorithm");

    public final String symbol;
    public final String name;

    LayerCategory(String symbol, String name) {
        this.symbol = symbol;
        this.name = name;
    }

};