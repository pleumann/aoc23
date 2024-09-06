package day18;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Day 11 "Lavaduct Lagoon".
 */
public class Puzzle {

    /**
     * A record to hold an instruction from our file.
     */
    record Instruction(char dir, int len, int rgb) { };
    
    /**
     * The plan is a list of instructions.
     */
    ArrayList<Instruction> plan = new ArrayList();
    
    /**
     * Read the input file.
     */
    void setup(BufferedReader r) throws IOException {        
        String s = r.readLine();
        while (s != null) {
            String[] a = s.split(" ");
            
            char dir = a[0].charAt(0);
            int len = Integer.parseInt(a[1]);
            int rgb = Integer.parseInt(a[2].substring(2, 8), 16);
            
            plan.add(new Instruction(dir, len, rgb));
            
            System.out.printf(".");
            
            s = r.readLine();
        }
        
        System.out.printf("\n\nPlan contains %d instructions.\n", plan.size());
    }
    
    /**
     * Performs a recursive flood-fill. Required for part 1.
     */
    int flood(char[][] map, int x, int y) {
        if (x < 0 || x >= map.length || y < 0 || y >= map.length) {
            return 0;
        }
        
        if (map[x][y] == '#') {
            return 0;
        }
        
        map[x][y] = '#';
        
        return 1 + flood(map, x - 1, y) 
                 + flood(map, x, y - 1) 
                 + flood(map, x + 1, y) 
                 + flood(map, x, y + 1);
    }
    
    /**
     * Solves part 1 using flood-fill.
     */
    int part1() throws IOException {
        int size = 1000;
        
        char[][] map = new char[size][size];
        
        int x = size / 2;
        int y = size / 2;
        
        for (char[] c: map) {
            Arrays.fill(c, '.');
        }
        
        map[x][y] = '#';

        for (Instruction i: plan) {
            int dx = 0;
            int dy = 0;
                    
            switch (i.dir) {
                case 'U' -> {
                    dx = -1;
                }
                case 'L' -> {
                    dy = -1;
                }
                case 'D' -> {
                    dx = 1;
                }
                case 'R' -> {
                    dy = 1;
                }
            }
            
            for (int j = 0; j < i.len; j++) {
                x = x + dx;
                y = y + dy;
                
                if (map[x][y] != '#') {
                    map[x][y] = '#';
                }
            }
        }
            
        return size * size - flood(map, 0, 0);
    }
    
    /**
     * Solves part 2 using Gauss' triangle (aka shoelace) formula.
     */
    long part2() {
        record Point(long x ,long y) { }

        ArrayList<Point> points = new ArrayList();

        long x = 0;
        long y = 0;
        long z = 0;
        
        points.add(new Point(x, y));

        for (Instruction i: plan) {
            switch (i.rgb % 16) {
                case 0 -> { y += i.rgb / 16; }
                case 1 -> { x += i.rgb / 16; }
                case 2 -> { y -= i.rgb / 16; }
                case 3 -> { x -= i.rgb / 16; }
            }
            
            points.add(new Point(x, y));
            z += i.rgb / 16;
        }

        long a = 0;
        
        for (int i = points.size() - 1; i > 0; i--) {
            Point p = points.get(i);
            Point q = points.get(i - 1);
            a = a + (p.y + q.y) * (p.x - q.x);
        }
        
        return Math.abs(a / 2) + z / 2 + 1;
    }
    
    /**
     * Provides the canonical entry point.
     */
    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println("*** AoC 2023.12 Lavaduct Lagoon ***");
        System.out.println();
        
        Puzzle p = new Puzzle();
        p.setup(new BufferedReader(new FileReader(args[0])));
        int i = p.part1();
        long l = p.part2();
                
        System.out.println();
        System.out.println("Part 1: " + i);
        System.out.println("Part 2: " + l);
        
        System.out.println();
    }
}
