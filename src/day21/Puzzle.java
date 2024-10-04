package day21;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Day 21 "Step Counter".
 */
public class Puzzle {
    
    /**
     * Number of repeats of the original map needed in each direction for
     * solving the puzzle.
     */
    static final int REPEATS = 5;
    
    /**
     * The full map.
     */
    char[][] map;
    
    /**
     * The size of the full map.
     */
    int mapSize;
    
    /**
     * The size of the original map.
     */
    int cellSize;

    /**
     * Performs a game-of-life-like step on the whole map, that is, moves all
     * plots to all their possible next locations.
     */
    void step() {
        char[][] mapNew = new char[mapSize][mapSize];
        
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                char c = map[i][j];
                
                if (c == '#') {
                    mapNew[i][j] = '#';
                } else if (c == '.') {
                    if (i > 0 && map[i - 1][j] == 'O') {
                        c = 'O';
                    } else if (i < mapSize - 1 && map[i + 1][j] == 'O') {
                        c = 'O';
                    } else if (j > 0 && map[i][j - 1] == 'O') {
                        c = 'O';
                    } else if (j < mapSize - 1 && map[i][j + 1] == 'O') {
                        c = 'O';
                    }
                    
                    mapNew[i][j] = c;
                } else if (c == 'O') {
                    mapNew[i][j] = '.';
                } else {
                    throw new RuntimeException("Unexpected character: " + c);
                }
            }
        }
        
        map = mapNew;
    }
    
    /**
     * Dumps the map.
     */
    void dump() {
        System.out.println();
        for (int i = 0; i < mapSize; i++) {
            System.out.println(new String(map[i]));
        }
    }

    /**
     * Returns the number of plot in this cell of the map.
     */
    int count(int cellX, int cellY) {
        int result = 0;
        
        for (int i = cellX * cellSize; i < (cellX + 1) * cellSize; i++) {
            for (int j = cellY * cellSize; j < (cellY + 1) * cellSize; j++) {
              if (map[i][j] == 'O') {
                  result++;
              }
            }
        }
        
        return result;
    }
    
    /**
     * Solves the puzzle for the input coming from the given reader.
     */
    void solve(BufferedReader r) throws IOException {
        // Load the original map an duplicate it in horizontal and vertical
        // directions.
        String s = r.readLine();
        
        cellSize = s.length();
        mapSize = REPEATS * cellSize;
        
        map = new char[mapSize][];
        
        for (int i = 0; i < cellSize; i++) {
            s = s.replace('S', '.').repeat(REPEATS);
            
            for (int j = 0; j < REPEATS; j++) {
                map[j * cellSize + i] = s.toCharArray();
            }
            
            s = r.readLine();
        }

        map[mapSize / 2][mapSize / 2] = 'O';

        // Do exactly the number of steps needed to reach each of the four
        // edges.
        long part1 = 0;
        for (int i = 0; i < REPEATS / 2 * 131 + 65; i++) {
            step();
            
            if (i == 63) {
                part1 = count(2, 2);
            }
        }

        // Dump the current state and collect the necessary information from the
        // 25 cells of the map.
        dump();

        System.out.println();
        for (int i = 0; i < REPEATS; i++) {
            for (int j = 0; j < REPEATS; j++) {
                System.out.printf("%10d ", count(i, j));
            }
            System.out.println();
        }
        
        long center1    = count(2, 2);
        long center2    = count(3, 2);
        
        long north      = count(0, 2);
        long south      = count(4, 2);
        long west       = count(2, 0);
        long east       = count(2, 4);
        
        long northWest1 = count(0, 1);
        long northWest2 = count(1, 1);
        
        long southWest1 = count(4, 1);
        long southWest2 = count(3, 1);

        long northEast1 = count(0, 3);
        long northEast2 = count(1, 3);
        
        long southEast1 = count(4, 3);
        long southEast2 = count(3, 3);

        // Extrapolate the diamond shape we have from the 5x5 grid to the much
        // larger grid that we would have if we really had made all those steps.
        long part2 = 0;

        long times1 = 202300 - 1;
        long times2 = 202300;

        part2 += times1 * center1 + times2 * center2;
        part2 += west + east;

        times1--;
        times2--;

        while (times1 > 0 || times2 > 0) {
            if (times1 > 0) {
                part2 += 2 * times1 * center1;
            }
            if (times2 > 0) {
                part2 += 2 * times2 * center2;
            }

            part2 += northWest1 + northEast1 + southWest1 + southEast1;
            part2 += northWest2 + northEast2 + southWest2 + southEast2;

            times1--;
            times2--;
        }

        part2 += north + south;
        part2 += northWest1 + northEast1 + southWest1 + southEast1;
        
        // Done
        System.out.println();
        System.out.println("Part 1: " + part1);
        System.out.println("Part 2: " + part2);
    }
    
    /**
     * Provides the canonical entry point.
     */
    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println("*** AoC 2023.21 Step Counter ***");
        System.out.println();
        
        new Puzzle().solve(new BufferedReader(new FileReader(args[0])));
        
        System.out.println();
    }
}
