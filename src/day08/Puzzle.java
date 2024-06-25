package day08;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Day 8 "Haunted Wasteland".
 */
public class Puzzle {

    /**
     * The list of instructions.
     */
    char[] instructions;
    
    /**
     * Our step counter.
     */
    int steps;
    
    /**
     * A helper class for nodes.
     */
    class Node {
        final String name, left, right;
        
        /**
         * Creates a node from a line of text.
         */
        Node(String s) {
            name = s.substring(0, 3);
            left = s.substring(7, 10);
            right = s.substring(12, 15);
        }

        /**
         * Gets the next node according to the instructions, increases steps.
         */
        String getNext() {
            return instructions[steps++ % instructions.length] == 'L' ? left : right;
        }
    }

    /**
     * A mapping of names to nodes.
     */
    HashMap<String, Node> nodes = new HashMap();

    /**
     * Creates puzzle object, loads puzzle input from given reader.
     */    
    Puzzle(BufferedReader r) throws IOException {
        instructions = r.readLine().toCharArray();
        
        r.readLine(); // Blank line
        
        String s = r.readLine();
        while (s != null) {
            Node n = new Node(s);
            nodes.put(n.name, n);
            s = r.readLine();
        }
    }

    /**
     * Solves part 1.
     */
    long part1() {
        Node n = nodes.get("AAA");
        steps = 0;
        
        while (!"ZZZ".equals(n.name)) {
            n = nodes.get(n.getNext());
        }
        
        return steps;
    }

    /**
     * Utility function, returns greatest common divisor.
     */
    static long gcd(long a, long b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    /**
     * Utility function, returns least common multiple.
     */
    static long lcm(long a, long b) {
        return Math.abs(a * b) / gcd(a, b);
    }
    
    /**
     * Solves part 2.
     */    
    long part2() throws IOException {
        long result = 1;

        for (String id: nodes.keySet()) {
            if (id.endsWith("A")) {
                Node n = nodes.get(id);
                steps = 0;

                while (!n.name.endsWith("Z")) {
                    n = nodes.get(n.getNext());
                }

                result = lcm(result, steps);
            }
        }

        return result;
    }
    
    /**
     * Provides the canonical entry point.
     */
    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println("*** AoC 2023.08 Haunted Wasteland ***");
        System.out.println();
        
        Puzzle p = new Puzzle(new BufferedReader(new FileReader(args[0])));
        System.out.println("Part 1: " + p.part1());
        System.out.println("Part 1: " + p.part2());
        
        System.out.println();
    }
}
