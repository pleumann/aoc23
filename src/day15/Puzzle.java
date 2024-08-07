package day15;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Day 12 "Lens Library".
 */
public class Puzzle {

    /**
     * Represents a single lens.
     */
    record Lens(String label, int strength) { };
    
    /**
     * Holds the 256 open-ended buckets (aka boxes).
     */
    ArrayList<Lens>[] boxes = new ArrayList[256];

    /**
     * Creates the puzzle, initializes the data structure.
     */
    Puzzle() {
        for (int i = 0; i < boxes.length; i++) {
            boxes[i] = new ArrayList();
        }
    }

    /**
     * Returns the hash value for the given string.
     */
    int hash(String s) {
        int start = 0;
        
        for (char c: s.toCharArray()) {
            start = (start + (int)c) * 17 % 256;
        }
        
        return start;
    }

    /**
     * Returns the position of a given label in a bucket, or -1 if not found.
     */
    int find(ArrayList<Lens> box, String label) {
        for (int i = 0; i < box.size(); i++) {
            if (box.get(i).label().equals(label)) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * Dumps everything in the same format that the puzzle website uses.
     */
    void dump(String what) {
        System.out.println("After \"" + what + "\":");
        for (int i = 0; i < 256; i++) {
            ArrayList<Lens> a = boxes[i];
            if (!a.isEmpty()) {
                System.out.print("Box " + i + ":");
                for (Lens l: a) {
                    System.out.print(" [" + l.label() + " " + l.strength() + "]");
                }
                System.out.println();
            }
        }
        System.out.println();
    }

    /**
     * Calculates the result value (sum of focusing powers, weird formula).
     */
    int value() {
        int result = 0;
        
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < boxes[i].size(); j++) {
                result = result + (i + 1) * (j + 1) * boxes[i].get(j).strength;
            }
        }
        
        return result;
    }

    /**
     * Solves the puzzle for the input coming from the given reader.
     */
    void solve(BufferedReader r) throws IOException {
        int part1 = 0;
                
        String s = r.readLine();        
        String[] a = s.split(",");
        
        for (String t: a) {
            part1 = part1 + hash(t);
            
            int p = t.indexOf('=');
            if (p == -1) {
                String label = t.substring(0, t.length() - 1);
                int b = hash(label);
                ArrayList<Lens> box = boxes[b];
                int j = find(box, label);
                if (j != -1) {
                    box.remove(j);                                  // Remove
                }
            } else {
                String label = t.substring(0, p);
                int focal = Integer.parseInt(t.substring(p+ 1));
                int b = hash(label);
                ArrayList<Lens> box = boxes[b];
                int j = find(box, label);
                if (j != -1) {
                    box.set(j, new Lens(label, focal));             // Replace
                } else {
                    box.add(new Lens(label, focal));                // Append
                }
            }
            
            dump(t);
        }

        int part2 = value();
        
        System.out.println("Part 1: " + part1);
        System.out.println("Part 2: " + part2);
        System.out.println();
    }
    
    /**
     * Provides the canonical entry point.
     */
    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println("*** AoC 2023.15 Lens Library ***");
        System.out.println();
        
        new Puzzle().solve(new BufferedReader(new FileReader(args[0])));
    }
}
