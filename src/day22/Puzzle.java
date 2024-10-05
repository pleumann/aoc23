package day22;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Day 22 "Sand Slabs".
 */
public class Puzzle {

    /**
     * Represents the "3D Tetris" map containing the parts.
    */
    int[][][] map = new int[10][10][500];

    /**
     * The relation representing which parts a given part provides support for.
     */
    HashMap<Integer, HashSet<Integer>> supportFor = new HashMap();
    
    /**
     * The relation representing which parts a given part gets support from.
     */
    HashMap<Integer, HashSet<Integer>> supportedBy = new HashMap();

    /**
     * Analyzes levels z and z+1 and updates the support relations.
     */
    void dependencies(int z) {
        System.out.printf("Analyzing level %d:\n", z);
        
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                int a = map[x][y][z];
                int b = map[x][y][z + 1];
                
                if (a != b) {
                    if (a != 0 && !supportFor.containsKey(a)) {
                        supportFor.put(a, new HashSet());
                    }

                    if (b != 0 && !supportedBy.containsKey(b)) {
                        supportedBy.put(b, new HashSet());
                    }

                    if (a != 0 && b != 0 && !supportFor.get(a).contains(b)) {
                        System.out.printf("- %d supports %d\n", a, b);
                        supportFor.get(a).add(b);
                        supportedBy.get(b).add(a);
                    }
                }
            }
        }
    }
    
    /**
     * Finds all useless bricks. A brick is useless if all other bricks it
     * supports are supported by at least one other brick.
     */
    int useless() {        
        System.out.println("Finding useless bricks:");
        int redundant = 0;
        
        for (int i: supportFor.keySet()) {
            boolean needed = false;
            
            for (int j: supportFor.get(i)) {
                if (supportedBy.get(j).size() == 1) {
                    needed = true;
                    break;
                }
            }
                
            if (!needed) {
                System.out.printf("%d ", i);
                redundant++;
            }
        }            
        
        System.out.println();

        return redundant;
    }
    
    /**
     * Starts a disintegration chain at the given brick. Continues to
     * disintegrate other bricks that are supported only by already
     * disintegrated bricks.
     */
    int chain(int start, int number) {
        System.out.printf("[%d]", start);
        HashSet<Integer> falling = new HashSet();
        falling.add(start);
        
        boolean live = true;
        while (live) {
            live = false;
            
            for (int i = 1; i <= number; i++) {
                if (!falling.contains(i) && supportedBy.containsKey(i)) {
                    int remaining = supportedBy.get(i).size();
                    for (int j: supportedBy.get(i)) {
                        if (falling.contains(j)) {                            
                            remaining--;
                        }
                    }
                    
                    if (remaining == 0) {
                        System.out.printf(" -> %d", i);
                        falling.add(i);
                        live = true;
                    }
                }
            }
        }
        
        System.out.println();
        
        return falling.size() - 1;
    }
    
    /**
     * Dumps the given level.
     */
    void dump(int z) {
        System.out.printf("Content at z=%d:\n", z);
        for (int x = 0; x < 10; x++) {
            System.out.print("  ".repeat(10 - x));
            for (int y = 0; y < 10; y ++) {
                int part = map[x][y][z];
                if (part == 0) {
                    System.out.print("   .  ");
                } else {
                    System.out.printf("\033[7;3" + (part % 8) + "m%4d\033[0m  ", part);
                }
            }
            System.out.println();
        }
        System.out.println();
    }
    
    /**
     * Solves the puzzle for the input coming from the given reader.
     */
    void solve(BufferedReader r) throws IOException {

        int number = 0;
        int height = 0;

        // Reads the snapshot of all bricks.
        ArrayList<int[]> all = new ArrayList();
        String s = r.readLine();
        while (s != null) {
            String[] a = s.split(",|~");
            int[] v = new int[6];
            
            for (int i = 0; i < 6; i++) {
                v[i] = Integer.parseInt(a[i]);
            }
            
            all.add(v);
            System.out.printf("%4d,%4d,%4d -> %4d,%4d,%4d\n", v[0], v[1], v[2], v[3], v[4], v[5]);
            
                        
            s = r.readLine();
        }
        
        System.out.println();
        
        // Sorts bricks, so they fall in correct order.
        all.sort((int[] o1, int[] o2) -> o1[2] - o2[2]);

        // Drops the bricks, one at a time.
        for (int[] v: all) {
            number++;
            
            if (v[0] != v[3]) {
                // x axis extent > 1, all other axes extents = 1.
                assert(v[1] == v[4]);
                assert(v[2] == v[5]);
                
                boolean rest = false;
                while (!rest && v[2] > 0) {
                    for (int i = v[0]; i <= v[3]; i++ ) {
                        if (map[i][v[1]][v[2] - 1] != 0) {
                            rest = true;
                            break;
                        }
                    }
                    if (!rest) {
                        v[2]--;
                        v[5]--;
                    }
                }
                
                for (int i = v[0]; i <= v[3]; i++ ) {
                    map[i][v[1]][v[2]] = number;
                }
            } else if (v[1] != v[4]) {
                // y axis extent > 1, all other axes extents = 1.
                assert(v[0] == v[3]);
                assert(v[2] == v[5]);
                
                boolean rest = false;
                while (!rest && v[2] > 0) {
                    for (int i = v[1]; i <= v[4]; i++ ) {
                        if (map[v[0]][i][v[2] - 1] != 0) {
                            rest = true;
                            break;
                        }
                    }
                    if (!rest) {
                        v[2]--;
                        v[5]--;
                    }
                }
                
                for (int i = v[1]; i <= v[4]; i++ ) {
                    map[v[0]][i][v[2]] = number;
                }
            } else {
                // z axis extent > 1, all other axes extents = 1.
                assert(v[0] == v[3]);
                assert(v[1] == v[4]);
                
                while (v[2] > 0) {
                    if (map[v[0]][v[1]][v[2] - 1] != 0) {
                        break;
                    }
                    
                    v[2]--;
                    v[5]--;
                }
                
                for (int i = v[2]; i <= v[5]; i++ ) {
                    map[v[0]][v[1]][i] = number;
                }
            }
            
            System.out.printf("Part %d comes to rest at z=%d\n", number, v[2]);
            height = Math.max(height, v[5]);
        }
        
        System.out.println();
        
        // Dumps the whole map.
        for (int z = height; z >= 0; z--) {
            dump(z);
        }

        // Analyzes dependencies on each level.
        for (int z = 0; z < height + 1; z++) {
            dependencies(z);
        }
        
        System.out.println();

        // Solves parts 1 and 2.
        long part1 = useless();
        long part2 = 0;
        
        System.out.println();
        System.out.println("Disintegrating:");
        for (int i = 1; i <= number; i++) {
            part2 = part2 + chain(i, number);
        }
        
        System.out.println();
        System.out.println("Part 1: " + part1);
        System.out.println("Part 2: " + part2);
    }
    
    /**
     * Provides the canonical entry point.
     */
    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println("*** AoC 2023.22 Sand Slabs ***");
        System.out.println();
        
        new Puzzle().solve(new BufferedReader(new FileReader(args[0])));
        
        System.out.println();
    }
}
