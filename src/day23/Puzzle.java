package day23;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Day 23 "A Long Walk".
 */
public class Puzzle {

    /**
     * The size of the (square) map.
     */
    int size;
    
    /**
     * The map itself.
     */
    char[][] map;

    /**
     * Represents an edge in the graph we are building.
     */    
    class Edge {
        
        /**
         * Target node.
         */
        Node to;
        
        /**
         * Cost of traveling this edge.
         */
        int cost;

        /**
         * Creates a new edge with the given target and cost.
         */
        Edge(Node to, int cost) {
            this.to = to;
            this.cost = cost;
        }
        
        @Override
        public String toString() {
            return "--" + cost + "--> " + to;
        }
    }
    
    /**
     * Represents a node in the graph we are building.
     */    
    class Node {
        
        /**
         * The coordinates of the node.
         */
        int x, y;
        
        /**
         * All edges originating from this node.
         */
        ArrayList<Edge> edges = new ArrayList();
        
        /**
         * Create a new node for the given coordinates.
         */
        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }

    /**
     * All nodes in our graph, by their coordinate strings.
     */
    HashMap<String, Node> nodes = new HashMap();

    /**
     * The node representing the top left and bottom right corners.
     */
    Node start, goal;
    
    /**
     * Create a new node for the given coordinates and marks the map
     * accordingly.
     */
    Node createNode(int x, int y) {
        Node n = new Node(x, y);

        nodes.put(n.toString(), n);
        map[x][y] = '+';
        
        System.out.println("New node " + n);
        
        return n;
    }
    
    /**
     * Explores the map and finds all nodes. A node will be generated for fields
     * that are itself '.' and have fewer than 2 '#' walls (so it must be some
     * sort of crossing).
     */
    void findNodes() {
        start = createNode(0, 1);
        goal = createNode(size - 1, size - 2);
        
        for (int x = 1; x < size - 1; x++) {
            for (int y = 1; y < size - 1; y++) {
                if (map[x][y] != '#') {
                    int walls = 0;
                    if (map[x - 1][y] == '#') {
                        walls++;
                    }
                    if (map[x][y - 1] == '#') {
                        walls++;
                    }
                    if (map[x + 1][y] == '#') {
                        walls++;
                    }
                    if (map[x][y + 1] == '#') {
                        walls++;
                    }

                    if (walls < 2) {
                        createNode(x, y);
                    }
                }
            }
        }
        
        System.out.println();
    }

    /**
     * Finds next nodes from a given position and a (known previous position, so
     * we don't walk back). Keeps track of cost. Whenever a node is found an
     * edge is created that points to it.
     */
    Edge edgeToNextNode(int x, int y, int fromX, int fromY, int cost) {
        char c = map[x][y];
        
        if (c == '#') {
            return null;
        }

        if (c == '^' && fromX < x) {
            return null;
        }

        if (c == '<' && fromY < y) {
            return null;
        }

        if (c == 'v' && fromX > x) {
            return null;
        }

        if (c == '>' && fromY > y) {
            return null;
        }
        
        if (c == '+') {
            return new Edge(nodes.get("(" + x + "," + y + ")"), cost);
        }

        if (fromX != x - 1) {
            Edge e = edgeToNextNode(x - 1, y, x, y, cost + 1);
            if (e != null) {
                return e;
            }
        }

        if (fromY != y - 1) {
            Edge e = edgeToNextNode(x, y - 1, x, y, cost + 1);
            if (e != null) {
                return e;
            }
        }

        if (fromX != x + 1) {
            Edge e = edgeToNextNode(x + 1, y, x, y, cost + 1);
            if (e != null) {
                return e;
            }
        }

        if (fromY != y + 1) {
            Edge e = edgeToNextNode(x, y + 1, x, y, cost + 1);
            if (e != null) {
                return e;
            }
        }
        
        return null;
    }
    
    /**
     * Explores the map and finds all edges.
     */
    void findEdges() {
        for (Node n: nodes.values()) {
            if (n.x > 0) {
                Edge e = edgeToNextNode(n.x - 1, n.y, n.x, n.y, 1);
                if (e != null) {
                    System.out.println("New edge " + n + " " + e);
                    n.edges.add(e);
                }
            }
            
            if (n.y > 0) {
                Edge e = edgeToNextNode(n.x, n.y - 1, n.x, n.y, 1);
                if (e != null) {
                    System.out.println("New edge " + n + " " + e);
                    n.edges.add(e);
                }
            }

            if (n.x < size - 1) {
                Edge e = edgeToNextNode(n.x + 1, n.y, n.x, n.y, 1);
                if (e != null) {
                    System.out.println("New edge " + n + " " + e);
                    n.edges.add(e);
                }
            }
            
            if (n.y < size - 1) {
                Edge e = edgeToNextNode(n.x, n.y + 1, n.x, n.y, 1);
                if (e != null) {
                    System.out.println("New edge " + n + " " + e);
                    n.edges.add(e);
                }
            }
        }
        
        System.out.println();
    }
    
    /**
     * Recursively explores the graph and finds all paths from the given node
     * to a goal node. Keeps track of cost, worst cost so far and the set of
     * nodes already visited.
     */
    int explore(Node node, int cost, int worst, HashSet<Node> seen) {
        if (node == goal) {
            return Math.max(cost, worst);
        }
        
        for (Edge edge: node.edges) {
            if (!seen.contains(edge.to)) {
                seen.add(node);
                worst = explore(edge.to, cost + edge.cost, worst, seen);
                seen.remove(node);
            }
        }
        
        return worst;
    }
    
    /**
     * Dumps the map.
     */
    void dump() {
        for (char[] c: map) {
            System.out.println(new String(c));
        }
        
        System.out.println();
    }
    
    /**
     * Solves the puzzle for the input coming from the given reader.
     */
    void solve(BufferedReader r) throws IOException {
        String s = r.readLine();
        
        size = s.length();
        map = new char[size][];
        
        for (int i = 0; i < size; i++) {
            map[i] = s.toCharArray();
            s = r.readLine();
        }

        dump();
        
        findNodes();
        findEdges();
        
        dump();

        int part1 = explore(start, 0, 0, new HashSet());
        
        // Part 2 is basically part 1 without the traffic signs.
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if ("<>^v+".indexOf(map[i][j]) != -1) {
                    map[i][j] = '.';
                }
            }
        }
        
        nodes.clear();
        
        findNodes();
        findEdges();
        
        dump();
        
        int part2 = explore(start, 0, 0, new HashSet());
        
        System.out.println("Part 1: " + part1);
        System.out.println("Part 2: " + part2);
    }
    
    /**
     * Provides the canonical entry point.
     */
    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println("*** AoC 2023.23 A Long Walk ***");
        System.out.println();
        
        new Puzzle().solve(new BufferedReader(new FileReader(args[0])));
        
        System.out.println();
    }
}
