package org.example.graph;

public class Edge {
    public int u;
    public int v;
    public int w;

    public Edge(int u, int v, int w) {
        this.u = u;
        this.v = v;
        this.w = w;
    }

    @Override
    public String toString() {
        return "(" + u + " → " + v + ", w=" + w + ")";
    }
}

