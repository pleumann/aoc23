package day10;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Day 10 "Pipe Maze".
 */
public class Puzzle {
   
    /**
     * Holds the map.
     */
    char[][] map;
    
    /**
     * The width/height of the (square) map.
     */
    int size;

    /**
     * The starting row of the loop.
     */
    int startRow;
    
    /**
     * The starting column of the loop.
     */
    int startColumn;

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
     * Loads the puzzle input from the given reader.
     */
    void load(BufferedReader rd) throws IOException {        
        String s = "." + rd.readLine() + ".";
        size = s.length();
        map = new char[size][];
        
        map[0] = (".".repeat(size)).toCharArray();
        for (int i = 1; i < size - 1; i++) {
            map[i] = s.toCharArray();
            int j = s.indexOf('S');
            if (j != -1) {
                startRow = i;
                startColumn = j;
            }
            
            s = "." + rd.readLine() + ".";
        }
        map[size - 1] = (".".repeat(size)).toCharArray();
    }
        
    /**
     * Inflates a single character of the original map to 3x3 characters.
     */
    String[] inflate(char c) {
        return switch (c) {
            case '|' -> new String[] { ".*.", ".*.", ".*." };
            case '-' -> new String[] { "...", "***", "..." };
            case '7' -> new String[] { "...", "**.", ".*." };
            case 'F' -> new String[] { "...", ".**", ".*." };
            case 'J' -> new String[] { ".*.", "**.", "..." };
            case 'L' -> new String[] { ".*.", ".**", "..." };
            case 'S' -> new String[] { ".*.", "***", ".*." };
            default  -> new String[] { "...", "...", "..." };
        };
    }
    
    /**
     * Inflates the whole map.
     */
    void inflate() {
        char[][] newMap = new char[size * 3][size * 3];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size ; j++) {
                char c = map[i][j];
                
                String[] s = inflate(c);
                
                for (int k =0; k < 3; k++) {
                    System.arraycopy(s[k].toCharArray(), 0, newMap[3 * i + k], 3 * j, 3);
                }
            }
        }
        
        map = newMap;
        size = 3 * size;
        startRow = 3 * startRow + 1;
        startColumn = 3 * startColumn + 1;
    }
    
    /**
     * Performs a recursive flood-fill with given fill and stop characters.
     */
    void flood(int x, int y, char fill, char stop) {
        if (x < 0 || y < 0 || x == size || y == size) {
            return;
        }
        
        char c = map[x][y];
        
        if (c == fill || c == stop) {
            return;
        }
                
        map[x][y] = fill;
        
        flood(x - 1, y, fill, stop);
        flood(x + 1, y, fill, stop);
        flood(x, y - 1, fill, stop);
        flood(x, y + 1, fill, stop);
    }
    
    /**
     * Solves the puzzle for the input coming from the given reader.
     */
    void solve(BufferedReader r) throws IOException {
        load(r);
        dump();

        System.out.println("Map size is " + size + " x " + size + ".");
        System.out.println("Starting position is (" + startRow + ", " + startColumn + ").");
        System.out.println();        

        System.out.println("Inflating map...");
        System.out.println();
        
        inflate();
        dump();

        System.out.println("Filling loop...");
        System.out.println();
        
        flood(startRow, startColumn, '#', '.');
        dump();
        
        System.out.println("Filling outside...");
        System.out.println();
        
        flood(0, 0, 'O', '#');
        dump();

        int part1 = 0;
        int part2 = 0;
        for (int i = 1; i < size; i += 3) {
            for (int j = 1; j < size; j += 3) {
                char c = map[i][j];
                if (c == '#') {
                    part1++;
                } else if (c != 'O') {
                    part2++;
                }
            }
        }
        
        System.out.println("Part 1: " + part1 / 2);
        System.out.println("Part 2: " + part2);
    }
    
    /**
     * Provides the canonical entry point.
     */
    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println("*** AoC 2023.10 Pipe Maze ***");
        System.out.println();
        
        new Puzzle().solve(new BufferedReader(new FileReader(args[0])));
        
        System.out.println();
    }
}
