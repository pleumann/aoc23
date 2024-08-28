package day17;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * Day 17 "Clumsy Crucible".
 */
public class Puzzle {
    
    /**
     * Represents a coordinate on the map (for in collections).
     */
    record XY(int x, int y) { }
    
    /**
     * Height of the map.
     */
    int height;
    
    /**
     * Width of the map.
     */
    int width;
    
    /**
     * The map itself.
     */
    char[][] map;

    /**
     * A 2D array of hashmaps mapping traces to best cost. Because of the puzzle
     * rules a single best value per coordinate is not sufficient.
     */
    HashMap<String, Integer> best[][];

    /**
     * The invalid trace regex that applies
     */
    Pattern invalid;
    
    /**
     * The maximum trace length that applies.
     */
    int length;
    
    /**
     * Invalid trace regex for part 1.
     */
    static String PART_1 = "\\^{4}|<{4}|v{4}|>{4}|(\\^v|v\\^|<>|><).*";

    /**
     * Invalid trace regex for part 2.
     */
    static String PART_2 = "([<>]\\^{1,3}[<>]|[\\^v]<{1,3}[\\^v]|[<>]v{1,3}[<>]|[\\^v]>{1,3}[\\^v]).*|\\^{11}|<{11}|v{11}|>{11}|(\\^v|v\\^|<>|><).*";

    /**
     * Creates a puzzle instance for the given invalid trace regex and maximum
     * trace length.
     */
    Puzzle(String regex, int length) {
        invalid = Pattern.compile(regex);
        this.length = length;
    }

    /**
     * Gets the cost for traveling the given field. The usual ASCII trick.
     */
    int getCost(int row, int column) {
        return map[row][column] - '0';
    }

    /**
     * Tries to update the best known cost for the given field and trace.
     * Returns true if an update was made.
     */
    boolean update(int row, int column, String trace, int cost) {
        if (best[row][column].getOrDefault(trace, 999999) > cost) {
            best[row][column].put(trace, cost);
            return true;
        }
        
        return false;
    }
    
    /**
     * Checks the validity of the given trace (most recent in front) and prunes
     * it if necessary (> 3 or > 10 characters). Returns the prunes trace or
     * null if that trace was invalid.
     */
    String check(String trace) {
        if (invalid.matcher(trace).matches()) {
            return null;
        }
        
        if (trace.length() > length) {
            return trace.substring(0, length);
        }
        
        return trace;
    }

    /**
     * Performs a single iteration, trying to find better cost values for all
     * fields by considering to come there from any of the four neighbours.
     * Returns true to indicate a change was made (and we need another round).
     */
    void think() {
        HashSet<XY> queue = new HashSet();
        
        queue.add(new XY(0, 0));

        while (!queue.isEmpty()) {
            System.out.print('.');

            HashSet<XY> queue2 = new HashSet();
            
            for (XY xy: queue) {
                int i = xy.x;
                int j = xy.y;
                
                HashMap<String, Integer> h = best[i][j];
                for (String s: h.keySet()) {
                    if (i > 0) {
                        String t = check("^" + s);
                        if (t != null && update(i - 1, j, t, h.get(s) + getCost(i - 1, j))) {
                            queue2.add(new XY(i - 1, j));
                        }
                    }

                    if (j > 0) {
                        String t = check("<" + s);
                        if (t != null && update(i, j - 1, t, h.get(s) + getCost(i, j - 1))) {
                            queue2.add(new XY(i, j - 1));
                        }
                    }
                    
                    if (i < height - 1) {
                        String t = check("v" + s);                        
                        if (t != null && update(i + 1, j, t, h.get(s) + getCost(i + 1, j))) {
                            queue2.add(new XY(i + 1, j));
                        }
                    }

                    if (j < width - 1) {
                        String t = check(">" + s);
                        if (t != null && update(i, j + 1, t, h.get(s) + getCost(i, j + 1))) {
                            queue2.add(new XY(i, j + 1));
                        }
                    }
                }
            }
            
            queue = queue2;
        }
    }
    
    /**
     * Backwards-draws the full trace into the map, making it look exactly like
     * the website examples.
     */
    void draw(int x, int y, String t, int cost) {
        while (x != 0 || y != 0) {
            int c = getCost(x, y);
            map[x][y] = t.charAt(0);
            
            
            switch (t.charAt(0)) {
                case 'v' -> x--;
                case '>' -> y--;
                case '^' -> x++;
                case '<' -> y++;
            }
            
            var field = best[x][y];
            for (String k: field.keySet()) {
                if (t.equals(check(t.charAt(0) + k)) && field.get(k) + c == cost) {
                    t = k;
                    cost = field.get(k);
                    break;
                }
            }
        }
    }
    
    /**
     * Solves the puzzle for the input coming from the given reader.
     */
    int solve(BufferedReader r) throws IOException {
        String s = r.readLine();
        width = s.length();

        ArrayList<char[]> temp = new ArrayList();
        
        while (s != null) {
            temp.add(s.toCharArray());
            s = r.readLine();
        }
        
        height = temp.size();
        map = temp.toArray(new char[height][]);
        
        best = new HashMap[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                best[i][j] = new HashMap();
            }
        }
        
        var start = best[0][0];
        var end = best[height - 1][width - 1];
                
        start.put("", 0);
        
        think();

        System.out.println();
        System.out.println();

        int result = 999999;
        String trace = null;
        
        for (String k: end.keySet()) {
            if (check("." + k) != null && end.get(k) < result) {
                result = end.get(k);
                trace = k;
            }
        }
        
        draw(height - 1, width - 1, trace, result);
        
        for (char[] c: map) {
            for (char d: c) {
                if (Character.isDigit(d)) {
                    System.out.print(d);
                } else {
                    System.out.print("\033[7m" + d + "\033[m");
                }
            }
            System.out.println();
        }
        
        System.out.println();
        
        return result;
    }
    
    /**
     * Provides the canonical entry point.
     */
    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println("*** AoC 2023.17 Clumsy Crucible ***");
        System.out.println();
        
        int part1 = new Puzzle(PART_1, 3).solve(new BufferedReader(new FileReader(args[0])));
        System.out.println("Part 1: " + part1);
        System.out.println();
        
        int part2 = new Puzzle(PART_2, 10).solve(new BufferedReader(new FileReader(args[0])));
        System.out.println("Part 2: " + part2);
        System.out.println();
    }
}
