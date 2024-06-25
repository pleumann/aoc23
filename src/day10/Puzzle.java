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
     * The width/height the (square) map.
     */
    int size;

    /**
     * The starting row of the loop.
     */
    int startRow;
    
    /**
     * The starting column of the loop.
     */
    int startCol;

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
                startCol = j;
            }
            
            s = "." + rd.readLine() + ".";
        }
        map[size - 1] = (".".repeat(size)).toCharArray();
    }
    
    /**
     * Fixes the start position of the loop so it is a pipe symbol.
     */
    void fixStart() {
        boolean up    = "|7F".indexOf(map[startRow - 1][startCol]) != -1;
        boolean down  = "|LJ".indexOf(map[startRow + 1][startCol]) != -1;
        boolean left  = "-LF".indexOf(map[startRow][startCol - 1]) != -1;
        boolean right = "-7J".indexOf(map[startRow][startCol + 1]) != -1;
        
        char c = 'S';
        if (up && down) {
            c = '|';
        } else if (left && right) {
            c = '-';
        } else if (up && right) {
            c = 'L';
        } else if (up && left) {
            c = 'J';
        } else if (down && left) {
            c = '7';
        } else if (down && right) {
            c = 'F';
        } else {
            throw new RuntimeException("WTF!?");
        }
        
        map[startRow][startCol] = c;
    }

        
    /**
     * Inflates a single character of the original map to 3x3 characters.
     */
    char[][] inflate(char c) {
        return switch (c) {
            case '|' -> new char[][] { { '.', '|', '.' }, 
                                       { '.', '|', '.' },
                                       { '.', '|', '.' } };
            case '-' -> new char[][] { { '.', '.', '.' }, 
                                       { '-', '-', '-' },
                                       { '.', '.', '.' } };
            case '7' -> new char[][] { { '.', '.', '.' }, 
                                       { '-', '7', '.' },
                                       { '.', '|', '.' } };
            case 'F' -> new char[][] { { '.', '.', '.' }, 
                                       { '.', 'F', '-' },
                                       { '.', '|', '.' } };
            case 'J' -> new char[][] { { '.', '|', '.' }, 
                                       { '-', 'J', '.' },
                                       { '.', '.', '.' } };
            case 'L' -> new char[][] { { '.', '|', '.' }, 
                                       { '.', 'L', '-' },
                                       { '.', '.', '.' } };
            default  -> new char[][] { { '.', '.', '.' }, 
                                       { '.', '.', '.' },
                                       { '.', '.', '.' } };
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
                
                char[][] d = Puzzle.this.inflate(c);
                
                for (int k =0; k < 3; k++) {
                    System.arraycopy(d[k], 0, newMap[3 * i + k], 3 * j, 3);
                }
            }
        }
        
        map = newMap;
        size = 3 * size;
        startRow = 3 * startRow + 1;
        startCol = 3 * startCol + 1;
    }

    /**
     * Recursively walks the path, fills it with '#', returns count.
     */
    int walk(int row, int col) {
        char c = map[row][col];
        
        if (c == '#') {
            return 0;
        }
        
        map[row][col] = '#';
        
        return switch (c) {
            case '|' -> 1 + walk(row - 1, col) + walk(row + 1, col);
            case '-' -> 1 + walk(row, col - 1) + walk(row, col + 1);
            case 'L' -> 1 + walk(row - 1, col) + walk(row, col + 1);
            case '7' -> 1 + walk(row, col - 1) + walk(row + 1, col);
            case 'J' -> 1 + walk(row - 1, col) + walk(row, col - 1);
            case 'F' -> 1 + walk(row, col + 1) + walk(row + 1, col);
            default  -> throw new RuntimeException("WTF!?");
        };
    }
    
    /**
     * Performs a recursive flood-fill with 'O', returns count.
     */
    int flood(int x, int y) {
        if (x < 0 || y < 0 || x == size || y == size) {
            return 0;
        }
        
        char c = map[x][y];
        
        if (c == '#' || c == 'O') {
            return 0;
        }
                
        map[x][y] = 'O';
        
        return  1 + flood(x - 1, y) + flood(x + 1, y) + flood(x, y - 1) + flood(x, y + 1);
    }
    
    /**
     * Solves the puzzle for the input coming from the given reader.
     */
    void solve(BufferedReader r) throws IOException {
        load(r);
        fixStart();
        dump();

        System.out.println("Map size is " + size + " x " + size + ".");
        System.out.println("Starting position is (" + startRow + ", " + startCol + ").");
        System.out.println();        

        System.out.println("Inflating map...");
        System.out.println();
        
        inflate();
        dump();

        System.out.println("Walking loop...");
        System.out.println();
        
        int part1 = walk(startRow, startCol);
        dump();
        
        System.out.println("Flood-filling...");
        System.out.println();
        
        int part2 = size * size - part1 - flood(0,0);
        dump();

//        System.out.println("Part 1: " + part1 / 3 / 2);
//        System.out.println("Part 2: " + part2 / 9);
        
        part1 = 0;
        part2 = 0;
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
