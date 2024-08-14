package day16;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Day 16 "The Floor Will Be Lava".
 */
public class Puzzle {

    /**
     * Size of our (square) map.
     */
    int size;
    
    /**
     * The map itself.
     */
    char[][] map;
    
    /**
     * Bitmap of ways we've gone through this field already.
     */
    int[][] seen;
    
    /**
     * Simulates a beam originating at (x,y) traveling with (dx,dy). Returns the
     * number of newly energized fields. Keeps track of which fields we've
     * already traveled and in which directions, so we don't do it again (in
     * order to avoid unnecessary work or even infinite loops). Spawns two
     * recursive calls of the method when encountering a splitter "flat".
     */
    int beam(int x, int y, int dx, int dy) {
        int result = 0;
        
        while (x >= 0 && x < size && y >= 0 && y < size) {
            char c = map[x][y];

            if (seen[x][y] == 0) {
                result++;
            }
                
            int k = dx == -1 ? 1 : dy == - 1 ? 2 : dx == 1 ? 4 : 8;
            if ((seen[x][y] & k) != 0) {
                break;                                  // Been here, done that.
            }
            seen[x][y] |= k;
        
            if (c == '/') {
                int z = -dy;                            // Mirror type 1
                dy = -dx;
                dx = z;
            } else if (c == '\\') {                     // Mirror type 2
                int z = dy;
                dy = dx;
                dx = z;
            } else if (c == '-' && dx != 0) {           // Horizontal splitter 
                return result + beam(x, y - 1, 0, -1) 
                              + beam(x, y + 1, 0, 1);
            } else if (c == '|' && dy != 0) {           // Vertical splitter
                return result + beam(x - 1, y, -1, 0)
                              + beam(x + 1, y, 1, 0);
            }

            x = x + dx;
            y = y + dy;            
        }
        
        return result;
    }
    
    /**
     * Solves the puzzle for the input coming from the given reader.
     */
    void solve(BufferedReader r) throws IOException {
        String s = r.readLine();
        size = s.length();
        map = new char[size][size];
        seen = new int[size][size];
        
        for (int i = 0; i < size; i++) {
            map[i] = s.toCharArray();
            s = r.readLine();
        }
        
        int part1 = beam(0, 0, 0, 1);
        int part2 = 0;

        for (int i = 0; i < size; i++) {
            System.out.print('.');
            
            seen = new int[size][size];
            part2 = Math.max(part2, beam(i, 0, 0, 1));
            
            seen = new int[size][size];
            part2 = Math.max(part2, beam(i, size - 1, 0, -1));
            
            seen = new int[size][size];
            part2 = Math.max(part2, beam(0, i, 1, 0));
            
            seen = new int[size][size];
            part2 = Math.max(part2, beam(size - 1, i, -1, 0));
        }
        
        System.out.println();
        System.out.println();
        System.out.println("Part 1: " + part1);
        System.out.println("Part 2: " + part2);
    }
    
    /**
     * Provides the canonical entry point.
     */
    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println("*** AoC 2023.16 The Floor Will Be Lava ***");
        System.out.println();
        
        new Puzzle().solve(new BufferedReader(new FileReader(args[0])));
        
        System.out.println();
    }
}
