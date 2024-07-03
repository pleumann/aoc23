package day11;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Day 11 "Cosmic Expansion".
 */
public class Puzzle {
    
    /**
     * The raw map as we read it from the input file (plus some annotations).
     */
    char[][] map;

    /**
     * The size of the map (we know it to be square).
     */
    int size;

    /**
     * Something to properly represent a galaxy.
     */
    record Galaxy(int x, int y, int ex, int ey) {
    
        long realX(long factor) {
            return factor * ex + x;
        }
        
        long realY(long factor) {
            return factor * ey + y;
        }
        
        long distance(Galaxy other, long factor) {
            return Math.abs(realX(factor) - other.realX(factor)) 
                 + Math.abs(realY(factor) - other.realY(factor));
        }
    
    };
    
    /**
     * The universe itself.
     */
    ArrayList<Galaxy> universe = new ArrayList();
    
    /**
     * Loads the input coming from the given reader, annotates it and builds a
     * universe that we can use later.
     */
    void load(BufferedReader r) throws IOException {

        // Step 1: Load 2D map
        String s = r.readLine();
        size = s.length();
        map = new char[size][];
        
        for (int i = 0; i < size; i++) {
            if ("".equals(s.replace('.', ' ').trim())) {
                s = "-".repeat(s.length());
            }
            map[i] = s.toCharArray();
            s = r.readLine();
        }

        // Step 2: Annotate expansions
        for (int i = 0; i < size; i++) {
            boolean empty = true;
            for (int j = 0; j < size; j++) {
                if (map[j][i] == '#') {
                    empty = false;
                    break;
                }
            }
            if (empty) {
                for (int j = 0; j < size; j++) {
                    char c = map[j][i];
                    if (c == '.') {
                        map[j][i] = '|';
                    } else {
                        map[j][i] = '+';
                    }
                }
            }
        }   
        
        // Step 3: Build galaxies
        int ex = 0;
        for (int i = 0; i < size; i++) {
            if (map[i][0] == '-') {
                ex ++;
            } else {
                int ey = 0;
            
                for (int j = 0; j < size; j++) {
                    if (map[0][j] == '|') {
                        ey++;
                    } else if (map[i][j] == '#') {
                        universe.add(new Galaxy(i - ex, j - ey, ex, ey));
                    }
                }
            }

        }
    }
    
    /**
     * Dumps the map.
     */
    void dump() {
        for (int i = 0; i < size; i++) {
            System.out.println(new String(map[i]));
        }
        
        System.out.println();
        System.out.println("The universe contains " + universe.size() + " galaxies.");
    }
    
    /**
     * Queries the universe for a solution with a given expansion factor.
     */
    long query(long factor) {
        long result =0;
        for (int i = 0; i < universe.size(); i++) {
            Galaxy g = universe.get(i);
            for (int j = i + 1; j < universe.size(); j++) {
                Galaxy h = universe.get(j);
                result = result + g.distance(h, factor);
            }
        }
        
        return result;
    }

    /**
     * Solves both parts.
     */
    void solve(BufferedReader r) throws IOException {
        load(r);
        dump();
        
        System.out.println();
        System.out.println("Part 1: " + query(2));
        System.out.println("Part 2: " + query(1000000));
    }
    
    /**
     * Provides the canonical entry point.
     */
    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println("*** AoC 2023.11 Cosmic Expansion ***");
        System.out.println();
        
        new Puzzle().solve(new BufferedReader(new FileReader(args[0])));
        
        System.out.println();
    }
}
