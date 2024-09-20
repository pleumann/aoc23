package day14;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Day 14 "Parabolic Reflector Dish".
 */
public class Puzzle {
    
    /**
     * The state of the 2D map
     */
    char[][] map; 

    /**
     * Width and height of the map
     */
    int size;
    
    /**
     * A cache for speeding up part 2 (or, rather, making it possible).
     */
    HashMap<String, Long> memory = new HashMap();

    /**
     * Dumps the current state of the map and returns the total load.
     */
    int dump() {
        int result = 0;
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
            char c = map[i][j];
                System.out.print(c);
                if (c == 'O') {
                    result = result + size - i;
                }
            }
            System.out.println();
        }
        System.out.println();
        
        return result;
    }

    /**
     * Lets all rocks fall north (as in part 1).
     */
    void drop() {
        for (int i = 1; i < size; i++) {
            for (int j = 0; j < size; j++) {
                char c = map[i][j];
                if (c == 'O') {
                    int k = i;
                    while (k > 0 && map[k - 1][j] == '.') {
                        k--;
                    }
                    
                    if (k != i) {
                        map[i][j] = '.';
                        map[k][j] = 'O';
                    }
                }
            }
        }
    }
    
    /**
     * Rotates the map by 90 degrees clockwise.
     */
    void turn() {
        char[][] result = new char[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                result[j][size - i - 1] = map[i][j];
            }
        }

        map = result;
    }
    
    /**
     * Returns a hashable fingerprint for the current state of the map.
     */
    String fingerprint() {
        StringBuilder sb = new StringBuilder();
        
        for (char[] c: map) {
            sb.append(new String(c));
        }
        
        return sb.toString();
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
        
        drop();
        
        int part1 = dump();
        int part2 = 0;
        
        long i = 0;
        boolean cycle = false;
        while (i <= 1000000000) {
            System.out.println("After cycle " + i + ":");
            part2 = dump();

            if (!cycle) {
                String fp = fingerprint();
                if (memory.containsKey(fp)) {
                    long j = memory.get(fp);
                    long l = i - j;
                    
                    System.out.println("Been here before in cycle " + j + ", length is " + l + ". Fast forward...");
                    System.out.println();
                    
                    i = j + (1000000000 - j) / l * l; // <--- Integer division.
                    cycle = true;
                } else {
                    memory.put(fp, i);
                }
            }

            for (int k = 0; k < 4; k++) {
                drop();
                turn(); 
            }
            
            i++;
        }
        
        System.out.println("Part 1: " + part1);
        System.out.println("Part 2: " + part2);
    }
    
    /**
     * Provides the canonical entry point.
     */
    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println("*** AoC 2023.14 Parabolic Reflector Dish ***");
        System.out.println();
        
        new Puzzle().solve(new BufferedReader(new FileReader(args[0])));
        
        System.out.println();
    }
}
