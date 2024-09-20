package day25;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Day 25 "Snowverload" using Karger's algorithm.
 */
public class Puzzle {

    /**
     * Since we are applying a randomized algorithm we will need a bit of
     * randomness.
     */
    static Random rnd = new Random();

    /**
     * Helper class representing the nodes.
     */
    class Node {

        /**
         * The name of the node.
         */
        String name;

        /**
         * Contains all edges that are connected to this node.
         */
        ArrayList<Edge> edges = new ArrayList();
        
        /**
         * Reflects how many original nodes this one represents (after merging).
         */
        int weight = 1;

        /**
         * Creates a new, named node.
         */
        Node(String s) {
            name = s;
        }
        
        @Override
        public String toString() {
            return name + " (weight " + weight + ")";
        }
        
    }

    /**
     * Helper class representing the edges.
     */
    class Edge {
        
        /**
         * Contains the two nodes this edge connects.
         */
        Node node1, node2;

        /**
         * Creates a new edge that connects the given nodes.
         */
        Edge(Node n1, Node n2) {
            node1 = n1;
            node2 = n2;
        }
        
        /**
         * Redirects either of the edge's two nodes from source to destination.
         */
        void redirect(Node src, Node dst) {
            if (node1 == src) {
                node1 = dst;
            } else if (node2 == src) {
                node2 = dst;
            }
        }

        /**
         * Checks if this is connecting a node to itself (which we don't want).
         */
        boolean isSelfEdge() {
            return node1 == node2;
        }
        
        @Override
        public String toString() {
            return node1.name + " <-> " + node2.name;
        }
    }
    
    /**
     * Lists of all nodes and edges for easy access.
     */
    ArrayList<Node> nodes = new ArrayList();
    ArrayList<Edge> edges = new ArrayList();

    /**
     * Contracts an edge by merging its second node into its first node,
     * adapting all edges attached to that second node accordingly. We remove
     * all "self" edges that might emerge, but keep redundant edges (because
     * they are needed).
     */
    void contract(Edge e) {
        Node dst = e.node1;
        Node src = e.node2;
        
        //System.out.println("Merging node " + src + " into node " + dst);

        e.node1.weight += e.node2.weight;
        
        nodes.remove(src);
        edges.remove(e);
        
        for (Edge f: src.edges) {
            f.redirect(src, dst);
            if (f.isSelfEdge()) {
                dst.edges.remove(f);
                edges.remove(f);
            } else {
                dst.edges.add(f);
            }
        }
    }
    
    /**
     * Dumps the graph.
     */
    void dump() {
        System.out.println("Graph has " + nodes.size() + " nodes and " + edges.size() + " edges.");
        System.out.println();
        for (Node n: nodes) {
            System.out.println("Node: " + n);
            for (Edge e: n.edges) {
                System.out.println("  Edge: " + e);
            }
        }
        System.out.println();
    }

    /**
     * Loads the graph from the given reader.
     */
    void load(BufferedReader r) throws IOException {
        HashMap<String, Node> nodesByName = new HashMap();
    
        String s = r.readLine();
        while (s != null) {
            String[] a = s.split(": | ");
            Node n1 = nodesByName.get(a[0]);
            if (n1 == null) {
                n1 = new Node(a[0]);
                nodes.add(n1);
                nodesByName.put(a[0], n1);
            }
            
            for (int i = 1; i < a.length; i++) {
                Node n2 = nodesByName.get(a[i]);
                if (n2 == null) {
                    n2 = new Node(a[i]);
                    nodesByName.put(a[i], n2);
                    nodes.add(n2);
                }

                Edge e = new Edge(n1, n2);
                edges.add(e);
                
                n1.edges.add(e);
                n2.edges.add(e);
            }
            
            s = r.readLine();
        }
    }
        
    /**
     * Solves the puzzle for the input coming from the given reader.
     */
    void solve(BufferedReader r) throws IOException {
        load(r);

        System.out.print('.');
        
        while (nodes.size() > 2) {
            int i = rnd.nextInt(edges.size());
            contract(edges.get(i));
        }
        
        if (edges.size() == 3) {
            System.out.println();
            System.out.println();
            dump();
            
            System.out.println("Bingo!");
            
            int i = nodes.get(0).weight;
            int j = nodes.get(1).weight;
            
            System.out.println();
            System.out.println("Part 1: " + i + " * " + j + " = " + i * j);
            System.out.println();
            System.exit(1);
        }
    }
    
    /**
     * Provides the canonical entry point.
     */
    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println("*** AoC 2023.25 Snowverload ***");
        System.out.println();

        while (true) {
            new Puzzle().solve(new BufferedReader(new FileReader(args[0])));
        }
    }
}
